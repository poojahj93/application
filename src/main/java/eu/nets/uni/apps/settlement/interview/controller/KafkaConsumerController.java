package eu.nets.uni.apps.settlement.interview.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.nets.uni.apps.settlement.interview.entity.ExchangeRateEntity;
import eu.nets.uni.apps.settlement.interview.exception.ExRatesNotPresentAtTimeException;
import eu.nets.uni.apps.settlement.interview.exception.ExRatesNotPresentForBaseCurrencyException;
import eu.nets.uni.apps.settlement.interview.model.ExchangeAmount;
import eu.nets.uni.apps.settlement.interview.service.CurrencyExchangeService;

@RestController
@RequestMapping("/api/v1")
public class KafkaConsumerController {

	Logger logger = LoggerFactory.getLogger(KafkaConsumerController.class);

	@Autowired
	CurrencyExchangeService currencyExchangeService;

	@Autowired
	MongoTemplate mongoTemplate;

	@GetMapping("/exchange-rate/{base-currency}")
	public ResponseEntity<ExchangeRateEntity> findLatestExchangeRatesByBaseCurrency(
			@PathVariable("base-currency") String baseCurrency) throws ExRatesNotPresentForBaseCurrencyException {
		logger.debug("findLatestExchangeRatesByBaseCurrency request : {}", baseCurrency);

		ExchangeRateEntity latestExchangeRatesByBaseCurrency = currencyExchangeService
				.getLatestExchangeRatesByBaseCurrency(baseCurrency);

		return new ResponseEntity<ExchangeRateEntity>(latestExchangeRatesByBaseCurrency, HttpStatus.OK);
	}

	@GetMapping(value = "/exchange-rates/{base-currency}")
	public ResponseEntity<ExchangeRateEntity> findExchangeRatesByTime(
			@PathVariable("base-currency") String baseCurrency, @RequestParam("datetime") String datetime)
			throws ExRatesNotPresentForBaseCurrencyException, ExRatesNotPresentAtTimeException {
		logger.debug("findExchangeRatesByTime baseCurrency :{}, datetime: {}", baseCurrency, datetime);
		ExchangeRateEntity exchangeRatesByTime = currencyExchangeService.getExchangeRatesByTime(baseCurrency, datetime);
		return new ResponseEntity<ExchangeRateEntity>(exchangeRatesByTime, HttpStatus.OK);
	}

	// or

	@GetMapping(value = "/exchange-rates1/{base-currency}")
	public ResponseEntity<ExchangeRateEntity> findExchangeRatesByTime1(
			@PathVariable("base-currency") String baseCurrency,
			@RequestParam("datetime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Instant instant)
			throws ExRatesNotPresentForBaseCurrencyException, ExRatesNotPresentAtTimeException {
		logger.debug("findExchangeRatesByTime baseCurrency :{}, datetime: {}", baseCurrency, instant);
		ExchangeRateEntity exchangeRatesByTime = currencyExchangeService.getExchangeRatesByTime1(baseCurrency, instant);
		return new ResponseEntity<ExchangeRateEntity>(exchangeRatesByTime, HttpStatus.OK);
	}

	@GetMapping(value = "/exchange-rates/{base-currency}/{currency}")
	public ResponseEntity<ExchangeAmount> calculateCurrencyExchangeAmount(
			@PathVariable("base-currency") String baseCurrency, @PathVariable("currency") String currency,
			@RequestParam("base-currency-amount") BigDecimal baseCurrencyAmount)
			throws ExRatesNotPresentForBaseCurrencyException, ExRatesNotPresentAtTimeException {
		logger.debug("calculateCurrencyExchangeAmount baseCurrency :{}, currency: {}, baseCurrencyAmount:{} ",
				baseCurrency, currency, baseCurrencyAmount);
		ExchangeAmount exchangeAmount = currencyExchangeService.getExchangeAmount(baseCurrency, currency,
				baseCurrencyAmount);
		return new ResponseEntity<ExchangeAmount>(exchangeAmount, HttpStatus.OK);
	}

	@GetMapping(value = "/exchange-rates/{base-currency}/report")
	public ResponseEntity<Resource> downloadReport(@PathVariable("base-currency") String baseCurrency)
			throws ExRatesNotPresentForBaseCurrencyException, ExRatesNotPresentAtTimeException, IOException {
		logger.debug("downloadReport baseCurrency :{}, ", baseCurrency);
		String filename = "report.csv";
		InputStreamResource file = new InputStreamResource(currencyExchangeService.getDownloadReport(baseCurrency));

		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
				.contentType(MediaType.parseMediaType("application/csv")).body(file);
	}

}
