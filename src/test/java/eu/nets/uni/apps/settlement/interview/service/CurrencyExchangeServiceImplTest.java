package eu.nets.uni.apps.settlement.interview.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.nets.uni.apps.settlement.interview.InterviewApplication;
import eu.nets.uni.apps.settlement.interview.constants.Constants;
import eu.nets.uni.apps.settlement.interview.entity.ExchangeRateEntity;
import eu.nets.uni.apps.settlement.interview.entity.ExchangeRateEntryEntity;
import eu.nets.uni.apps.settlement.interview.exception.ExRatesNotPresentAtTimeException;
import eu.nets.uni.apps.settlement.interview.exception.ExRatesNotPresentForBaseCurrencyException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { InterviewApplication.class })
public class CurrencyExchangeServiceImplTest {

	@InjectMocks
	CurrencyExchangeServiceImpl service;

	@Mock
	CurrencyExchangeServiceImpl currencyExchangeServiceImpl;

	@Mock
	MongoTemplate template;

	@Test
	public void getLatestExchangeRatesByBaseCurrencyTest() throws ExRatesNotPresentForBaseCurrencyException {
		List<ExchangeRateEntryEntity> collect = Stream.of(new ExchangeRateEntryEntity("USD", new BigDecimal("1.0987")),
				new ExchangeRateEntryEntity("NOK", new BigDecimal(9.0987))).collect(Collectors.toList());

		List<ExchangeRateEntity> exRateEntity = Stream.of(new ExchangeRateEntity(Instant.now(), "EUR", collect))
				.collect(Collectors.toList());

		Query query = new Query();
		query.addCriteria(Criteria.where("baseCurrency").is("EUR"));
		query.with(Sort.by(Sort.Direction.DESC, "timestamp"));

		when(template.find(query, ExchangeRateEntity.class)).thenReturn(exRateEntity);
		assertEquals(1, exRateEntity.size());

		service.getLatestExchangeRatesByBaseCurrency("EUR");

		ExRatesNotPresentForBaseCurrencyException exRatesNotPresentForBaseCurrencyException = Assertions.assertThrows(
				ExRatesNotPresentForBaseCurrencyException.class,
				() -> service.getLatestExchangeRatesByBaseCurrency("USD"));
		assertEquals(Constants.EXCHANGE_RATE_NOT_FOUND, exRatesNotPresentForBaseCurrencyException.getMessage());

	}

	@Test
	public void getExchangeRatesByTimeTest()
			throws ExRatesNotPresentForBaseCurrencyException, ExRatesNotPresentAtTimeException {
		List<ExchangeRateEntryEntity> collect = Stream.of(new ExchangeRateEntryEntity("USD", new BigDecimal("1.0987")),
				new ExchangeRateEntryEntity("NOK", new BigDecimal(9.0987))).collect(Collectors.toList());

		List<ExchangeRateEntity> exRateEntity = Stream.of(new ExchangeRateEntity(Instant.now(), "EUR", collect))
				.collect(Collectors.toList());

		Query query = new Query();
		query.addCriteria(Criteria.where("baseCurrency").is("EUR"));

		when(template.find(query, ExchangeRateEntity.class)).thenReturn(exRateEntity);
		assertEquals(1, exRateEntity.size());

		// service.getExchangeRatesByTime("EUR", "2022-08-12T10:57:40");

		ExRatesNotPresentAtTimeException exRatesNotPresentAtTimeException = Assertions.assertThrows(
				ExRatesNotPresentAtTimeException.class,
				() -> service.getExchangeRatesByTime("EUR", "2022-08-12T10:57:40"));
		assertEquals(Constants.EXCHANGE_RATE_NOT_FOUND_TIME, exRatesNotPresentAtTimeException.getMessage());

	}

	@Test
	public void getExchangeAmountTest()
			throws ExRatesNotPresentAtTimeException, ExRatesNotPresentForBaseCurrencyException {
		List<ExchangeRateEntryEntity> collect = Stream.of(new ExchangeRateEntryEntity("USD", new BigDecimal("1.0987")),
				new ExchangeRateEntryEntity("NOK", new BigDecimal(9.0987))).collect(Collectors.toList());

		List<ExchangeRateEntity> exRateEntity = Stream.of(new ExchangeRateEntity(Instant.now(), "EUR", collect))
				.collect(Collectors.toList());

		Query query = new Query();
		query.addCriteria(Criteria.where("baseCurrency").is("EUR"));

		when(template.find(query, ExchangeRateEntity.class)).thenReturn(exRateEntity);
		assertEquals(1, exRateEntity.size());

		service.getExchangeAmount("EUR", "USD", new BigDecimal("80"));

		ExRatesNotPresentForBaseCurrencyException exRatesNotPresentForBaseCurrencyException = Assertions.assertThrows(
				ExRatesNotPresentForBaseCurrencyException.class,
				() -> service.getExchangeAmount("EURR", "USD", new BigDecimal("80")));
		assertEquals(Constants.EXCHANGE_RATE_NOT_FOUND, exRatesNotPresentForBaseCurrencyException.getMessage());

		ExRatesNotPresentForBaseCurrencyException exRatesNotPresentForBaseCurrencyException2 = Assertions.assertThrows(
				ExRatesNotPresentForBaseCurrencyException.class,
				() -> service.getExchangeAmount("EURR", "USDD", new BigDecimal("80")));
		assertEquals(Constants.EXCHANGE_RATE_NOT_FOUND, exRatesNotPresentForBaseCurrencyException2.getMessage());

	}

	@Test
	public void consumeAndSaveDbTest() throws JsonMappingException, JsonProcessingException {
		List<ExchangeRateEntryEntity> collect = Stream.of(new ExchangeRateEntryEntity("USD", new BigDecimal("1.0987")),
				new ExchangeRateEntryEntity("NOK", new BigDecimal(9.0987))).collect(Collectors.toList());

		ExchangeRateEntity exchangeRateEntity = new ExchangeRateEntity(Instant.now(), "EUR", collect);

		when(template.insert(Mockito.any(ExchangeRateEntity.class))).thenReturn(exchangeRateEntity);

		String sample = "{\"timestamp\":1660138880.002259800,\"baseCurrency\":\"EUR\",\"exchangeRateEntries\":[{\"currency\":\"USD\",\"rate\":1.0172},{\"currency\":\"JPY\",\"rate\":129.6518},{\"currency\":\"BGN\",\"rate\":2.0129},{\"currency\":\"CZK\",\"rate\":24.9413},{\"currency\":\"DKK\",\"rate\":7.4700},{\"currency\":\"GBP\",\"rate\":0.7877},{\"currency\":\"HUF\",\"rate\":371.4018},{\"currency\":\"PLN\",\"rate\":4.6446},{\"currency\":\"RON\",\"rate\":5.0448},{\"currency\":\"SEK\",\"rate\":10.4301},{\"currency\":\"CHF\",\"rate\":1.0432},{\"currency\":\"ISK\",\"rate\":144.8793},{\"currency\":\"NOK\",\"rate\":9.8384},{\"currency\":\"HRK\",\"rate\":7.5442},{\"currency\":\"TRY\",\"rate\":16.1062},{\"currency\":\"AUD\",\"rate\":1.4806},{\"currency\":\"BRL\",\"rate\":5.6948},{\"currency\":\"CAD\",\"rate\":1.3112},{\"currency\":\"CNY\",\"rate\":7.0327},{\"currency\":\"HKD\",\"rate\":8.6202},{\"currency\":\"IDR\",\"rate\":15710.3520},{\"currency\":\"ILS\",\"rate\":3.5479},{\"currency\":\"INR\",\"rate\":83.9121},{\"currency\":\"KRW\",\"rate\":1366.0793},{\"currency\":\"MXN\",\"rate\":23.0199},{\"currency\":\"MYR\",\"rate\":4.5570},{\"currency\":\"NZD\",\"rate\":1.6326},{\"currency\":\"PHP\",\"rate\":57.4739},{\"currency\":\"SGD\",\"rate\":1.5163},{\"currency\":\"THB\",\"rate\":36.8072},{\"currency\":\"ZAR\",\"rate\":16.5836}]}";
		service.consumeAndSaveDb(sample);

	}

	@Test
	public void getDownloadReportTest() throws IOException {

		List<ExchangeRateEntryEntity> collect = Stream.of(new ExchangeRateEntryEntity("USD", new BigDecimal("1.0987")),
				new ExchangeRateEntryEntity("NOK", new BigDecimal(9.0987))).collect(Collectors.toList());

		List<ExchangeRateEntity> exRateEntity = Stream.of(new ExchangeRateEntity(Instant.now(), "EUR", collect))
				.collect(Collectors.toList());

		Query query = new Query();
		query.addCriteria(Criteria.where("baseCurrency").is("EUR"));
		query.with(Sort.by(Sort.Direction.DESC, "timestamp"));

		when(template.find(query, ExchangeRateEntity.class)).thenReturn(exRateEntity);
		assertEquals(1, exRateEntity.size());

		service.getDownloadReport("EUR");

	}

	@Test
	public void getExchangeRatesByTime1Test() throws ExRatesNotPresentAtTimeException {

		List<ExchangeRateEntryEntity> collect = Stream.of(new ExchangeRateEntryEntity("USD", new BigDecimal("1.0987")),
				new ExchangeRateEntryEntity("NOK", new BigDecimal(9.0987))).collect(Collectors.toList());

		ExchangeRateEntity exRateEntity = new ExchangeRateEntity(Instant.now(), "EUR", collect);

		Query query = new Query();
		query.addCriteria(Criteria.where("baseCurrency").is("EUR"));
		query.addCriteria(Criteria.where("timestamp").is(Instant.now()));

		when(template.findOne(query, ExchangeRateEntity.class)).thenReturn(exRateEntity);
		assertEquals("EUR", exRateEntity.getBaseCurrency());

		ExRatesNotPresentAtTimeException exRatesNotPresentAtTimeException = Assertions.assertThrows(
				ExRatesNotPresentAtTimeException.class, () -> service.getExchangeRatesByTime1("EUR", Instant.now()));
		assertEquals(Constants.EXCHANGE_RATE_NOT_FOUND_TIME, exRatesNotPresentAtTimeException.getMessage());

	}

}
