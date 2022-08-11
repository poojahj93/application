package eu.nets.uni.apps.settlement.interview.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;

import eu.nets.uni.apps.settlement.interview.constants.Constants;
import eu.nets.uni.apps.settlement.interview.entity.ExchangeRateEntity;
import eu.nets.uni.apps.settlement.interview.entity.ExchangeRateEntryEntity;
import eu.nets.uni.apps.settlement.interview.exception.ExRatesNotPresentAtTimeException;
import eu.nets.uni.apps.settlement.interview.exception.ExRatesNotPresentForBaseCurrencyException;
import eu.nets.uni.apps.settlement.interview.model.CsvPojo;
import eu.nets.uni.apps.settlement.interview.model.ExchangeAmount;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRateEntry;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;
import eu.nets.uni.apps.settlement.interview.repository.ExchangeRateRepository;

@Service
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

	Logger logger = LoggerFactory.getLogger(CurrencyExchangeServiceImpl.class);

	@Autowired
	public ExchangeRateRepository exchangeRateRepository;

	@KafkaListener(id = "exchange-rates", topics = "${interview.kafka-topic-exchange-rates}")
	@Override
	public void consumeAndSaveDb(String jsonMessage) throws JsonMappingException, JsonProcessingException {
		logger.info("Got message: {}", jsonMessage);
		Gson gson = new Gson();
		ExchangeRates exchangeRates = gson.fromJson(jsonMessage, ExchangeRates.class);
		long l = exchangeRates.getTimestamp().longValue();
		Instant ofEpochSecond = Instant.ofEpochSecond(l);
		System.out.println("ofEpochSecond value is..." + ofEpochSecond);
		ExchangeRateEntity exchangeRateEntity = convertPojoToEntity(exchangeRates);
		exchangeRateEntity.setTimestamp(ofEpochSecond);
		logger.debug("Consumed timestamp of exchange rates :{}", ofEpochSecond);
		ExchangeRateEntity save = exchangeRateRepository.save(exchangeRateEntity);
		logger.debug("came to end..." + save.getTimestamp());

	}

	@Override
	public ExchangeRateEntity getLatestExchangeRatesByBaseCurrency(String baseCurrency)
			throws ExRatesNotPresentForBaseCurrencyException {
		List<ExchangeRateEntity> findByBaseCurrency = exchangeRateRepository.findByBaseCurrency(baseCurrency);
		ExchangeRateEntity orElseThrow = findByBaseCurrency.stream()
				.sorted(Comparator.comparing(ExchangeRateEntity::getTimestamp).reversed()).findFirst()
				.orElseThrow(() -> new ExRatesNotPresentForBaseCurrencyException(Constants.EXCHANGE_RATE_NOT_FOUND));
		return orElseThrow;
	}

	@Override
	public ExchangeRateEntity getExchangeRatesByTime(String baseCurrency, String dateTime)
			throws ExRatesNotPresentForBaseCurrencyException, ExRatesNotPresentAtTimeException {
		LocalDateTime incomingLocalDateTime = LocalDateTime.parse(dateTime);
		ExchangeRateEntity orElseThrow = exchangeRateRepository.findByBaseCurrency(baseCurrency).parallelStream()
				.filter(exchangeRateEntity -> LocalDateTime.ofInstant(exchangeRateEntity.getTimestamp(), ZoneOffset.UTC)
						.toString().equals(incomingLocalDateTime.toString()))
				.findFirst()
				.orElseThrow(() -> new ExRatesNotPresentAtTimeException(Constants.EXCHANGE_RATE_NOT_FOUND_TIME));
		return orElseThrow;
	}

	@Override
	public ByteArrayInputStream getDownloadReport(String baseCurrency) throws IOException {

		ExchangeRateEntity findFirst = exchangeRateRepository.findByBaseCurrency(baseCurrency).parallelStream()
				.sorted(Comparator.comparing(ExchangeRateEntity::getTimestamp).reversed()).findFirst().get();

		LocalDateTime of = getConvertedLocalDateTime(findFirst.getTimestamp());
		logger.debug("Laaest record timestamp :{}", of);
		List<ExchangeRateEntity> exchangeRateEntities = new ArrayList<>();
		int i = 0;
		while (i < 10) {
			LocalDateTime minusMinutes = of.minusMinutes(i);
			logger.debug("minusMinutes dates from latest date :{}", minusMinutes );
			
			List<ExchangeRateEntryEntity> collect = exchangeRateRepository.findByBaseCurrency(baseCurrency)
					.parallelStream().sorted(Comparator.comparing(ExchangeRateEntity::getTimestamp).reversed())
					.filter(exchangeRateEnt -> getConvertedLocalDateTime(exchangeRateEnt.getTimestamp())
							.equals(minusMinutes))
					.map(exRate -> exRate.getExchangeRateEntries()).flatMap(exRtEntry -> exRtEntry.stream())
					.collect(Collectors.toList());

			logger.debug("size of ExchangeRateEntryEntity at given time :{}", collect.size());

			Map<String, Double> map = collect.stream()
					.collect(Collectors.groupingBy(ExchangeRateEntryEntity::getCurrency, LinkedHashMap::new,
							Collectors.mapping(ExchangeRateEntryEntity::getRate,
									Collectors.averagingDouble(BigDecimal::doubleValue))));

			List<ExchangeRateEntryEntity> exchangeRateEntryEntities = new ArrayList<>();
			map.entrySet().stream().forEach(e -> {
				ExchangeRateEntryEntity exchangeRateEntryEntity = new ExchangeRateEntryEntity();
				exchangeRateEntryEntity.setCurrency(e.getKey());
				exchangeRateEntryEntity.setRate(BigDecimal.valueOf(e.getValue()));
				exchangeRateEntryEntities.add(exchangeRateEntryEntity);
			});

			ExchangeRateEntity exchangeRateEntity = new ExchangeRateEntity();
			exchangeRateEntity.setBaseCurrency(baseCurrency);
			exchangeRateEntity.setTimestamp(minusMinutes.toInstant(ZoneOffset.UTC));
			exchangeRateEntity.setExchangeRateEntries(exchangeRateEntryEntities);
			exchangeRateEntities.add(exchangeRateEntity);
			i++;
		}
		logger.debug("Last 10 minitues of exchangeRateEntities size :{}" , exchangeRateEntities.size());
		List<CsvPojo> csvCompactibleJSon = csvCompactibleJSon(exchangeRateEntities);
		ByteArrayInputStream exchangeRateEntityToCSV = exchangeRateEntityToCSV(csvCompactibleJSon);
		return exchangeRateEntityToCSV;
	}

	public LocalDateTime getConvertedLocalDateTime(Instant dateTime) {

		LocalDateTime incomingDateTime = LocalDateTime.ofInstant(dateTime, ZoneOffset.UTC);
		LocalDateTime outgoingDateTime = LocalDateTime.of(incomingDateTime.getYear(), incomingDateTime.getMonthValue(),
				incomingDateTime.getDayOfMonth(), incomingDateTime.getHour(), incomingDateTime.getMinute());
		return outgoingDateTime;
	}

	public List<CsvPojo> csvCompactibleJSon(List<ExchangeRateEntity> exchangeRateEntities) {
		List<CsvPojo> listCsvPojo = new ArrayList<>();
		exchangeRateEntities.forEach(exchangeRateEntity -> {
			exchangeRateEntity.getExchangeRateEntries().forEach(exRateEntry -> {
				CsvPojo csvPojo = new CsvPojo();
				csvPojo.setBaseCurrency(exchangeRateEntity.getBaseCurrency());
				csvPojo.setTimeStamp(exchangeRateEntity.getTimestamp().toString());
				csvPojo.setCurrency(exRateEntry.getCurrency());
				csvPojo.setAvgRate(exRateEntry.getRate());
				listCsvPojo.add(csvPojo);
			});
		});
		return listCsvPojo;
	}

	public ByteArrayInputStream exchangeRateEntityToCSV(List<CsvPojo> csvPojos) {
		final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {

			List<String> data = Arrays.asList(Constants.BASE_CURRENCY, Constants.TIME_STAMP, Constants.CURRENCY, Constants.AVRAGE_RATE);
			csvPrinter.printRecord(data);
			for (CsvPojo csv : csvPojos) {
				data = Arrays.asList(csv.getBaseCurrency(), csv.getTimeStamp(), csv.getCurrency(),
						String.valueOf(csv.getAvgRate()));

				csvPrinter.printRecord(data);
			}

			csvPrinter.flush();
			return new ByteArrayInputStream(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
		}
	}

	@Override
	public ExchangeAmount getExchangeAmount(String baseCurrency, String currency, BigDecimal baseCurrencyAmount)
			throws ExRatesNotPresentAtTimeException, ExRatesNotPresentForBaseCurrencyException {
		ExchangeRateEntryEntity orElseThrow = exchangeRateRepository.findByBaseCurrency(baseCurrency).stream()
				.sorted(Comparator.comparing(ExchangeRateEntity::getTimestamp).reversed()).findFirst()
				.orElseThrow(() -> new ExRatesNotPresentForBaseCurrencyException(Constants.EXCHANGE_RATE_NOT_FOUND))
				.getExchangeRateEntries().parallelStream()
				.filter(exchangeRateEntries -> exchangeRateEntries.getCurrency().equals(currency)).findFirst()
				.orElseThrow(() -> new ExRatesNotPresentForBaseCurrencyException(Constants.CURRENCY_NOT_FOUND));
		ExchangeAmount exchangeAmount = new ExchangeAmount();
		exchangeAmount.setBaseCurrency(baseCurrency);
		exchangeAmount.setBaseCurrencyValue(baseCurrencyAmount);
		exchangeAmount.setCurrency(currency);
		exchangeAmount.setExchangeAmount(orElseThrow.getRate().multiply(baseCurrencyAmount));
		return exchangeAmount;
	}

	public ExchangeRateEntity convertPojoToEntity(ExchangeRates exchangeRates) {
		ExchangeRateEntity exchangeRateEntity = new ExchangeRateEntity();
		Optional.of(exchangeRates).ifPresent(exchangeRate -> {
			exchangeRateEntity.setBaseCurrency(exchangeRates.getBaseCurrency());
			List<ExchangeRateEntryEntity> collect = exchangeRates.getExchangeRateEntries().stream()
					.map(CurrencyExchangeServiceImpl::ConvertingPojoToEntity).collect(Collectors.toList());
			exchangeRateEntity.setExchangeRateEntries(collect);
		});
		return exchangeRateEntity;
	}

	public static ExchangeRateEntryEntity ConvertingPojoToEntity(ExchangeRateEntry exchangeRateEntry) {
		ExchangeRateEntryEntity exchangeRateEntryEntity = new ExchangeRateEntryEntity();
		exchangeRateEntryEntity.setRate(exchangeRateEntry.getRate());
		exchangeRateEntryEntity.setCurrency(exchangeRateEntry.getCurrency());
		return exchangeRateEntryEntity;
	}

}
