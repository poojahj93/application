package eu.nets.uni.apps.settlement.interview.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import eu.nets.uni.apps.settlement.interview.model.CsvPojo;
import eu.nets.uni.apps.settlement.interview.model.ExchangeAmount;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRateEntry;
import eu.nets.uni.apps.settlement.interview.model.ExchangeRates;

public class TestConstructorAndGetter {

	@Test
	public void testCsvPojo() {
		CsvPojo csvPojo1 = new CsvPojo("EUR", "2022-08-12T10:57:40", "USD", new BigDecimal("1234"));
		CsvPojo csvPojo = new CsvPojo();
		csvPojo.setAvgRate(new BigDecimal("1234"));
		csvPojo.setBaseCurrency("EUR");
		csvPojo.setCurrency("USD");
		csvPojo.setTimeStamp("2022-08-12T10:57:40");
		csvPojo.getAvgRate();
		csvPojo.getBaseCurrency();
		csvPojo.getCurrency();
		csvPojo.getTimeStamp();
		assertEquals("EUR", csvPojo.getBaseCurrency());
		assertEquals("USD", csvPojo1.getCurrency());
	}

	@Test
	public void testExchangeAmount() {
		ExchangeAmount exchangeAmount1 = new ExchangeAmount("EUR", new BigDecimal("80"), "USD", new BigDecimal("83"));
		ExchangeAmount exchangeAmount = new ExchangeAmount();
		exchangeAmount.setBaseCurrency("EUR");
		exchangeAmount.setCurrency("USD");
		exchangeAmount.setBaseCurrencyValue(new BigDecimal("80"));
		exchangeAmount.setExchangeAmount(new BigDecimal("83"));
		exchangeAmount.getBaseCurrency();
		exchangeAmount.getBaseCurrencyValue();
		exchangeAmount.getCurrency();
		exchangeAmount.getExchangeAmount();
		assertEquals("EUR", exchangeAmount.getBaseCurrency());
		assertEquals("USD", exchangeAmount1.getCurrency());

	}

	@Test
	public void testExchangeRateEntry() {
		ExchangeRateEntry exchangeRateEntry = new ExchangeRateEntry();
		ExchangeRateEntry exchangeRateEntry1 = new ExchangeRateEntry("USD", new BigDecimal("1234"));
		exchangeRateEntry.setCurrency("USD");
		exchangeRateEntry.setRate(new BigDecimal("1234"));
		exchangeRateEntry.getCurrency();
		exchangeRateEntry.getRate();
		assertEquals("USD", exchangeRateEntry1.getCurrency());
		assertEquals(new BigDecimal("1234"), exchangeRateEntry.getRate());
	}

	@Test
	public void testExchangeRates() {
		ExchangeRates exchangeRate = new ExchangeRates();
		exchangeRate.setBaseCurrency("EUR");
		exchangeRate.setTimestamp(1660138880.002259800);
		exchangeRate.setExchangeRateEntries(
				Stream.of(new ExchangeRateEntry("USD", new BigDecimal("1234"))).collect(Collectors.toList()));
		assertEquals("EUR", exchangeRate.getBaseCurrency());
		assertEquals(1, exchangeRate.getExchangeRateEntries().size());

	}

}
