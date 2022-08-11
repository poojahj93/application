package eu.nets.uni.apps.settlement.interview.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import eu.nets.uni.apps.settlement.interview.entity.ExchangeRateEntity;
import eu.nets.uni.apps.settlement.interview.exception.ExRatesNotPresentAtTimeException;
import eu.nets.uni.apps.settlement.interview.exception.ExRatesNotPresentForBaseCurrencyException;
import eu.nets.uni.apps.settlement.interview.model.ExchangeAmount;

public interface CurrencyExchangeService {

	
	public void consumeAndSaveDb(String jsonMessage) throws JsonMappingException, JsonProcessingException;
	
	public ExchangeRateEntity getLatestExchangeRatesByBaseCurrency(String baseCurrency) throws ExRatesNotPresentForBaseCurrencyException;
	
	public ExchangeRateEntity getExchangeRatesByTime(String baseCurrency, String dateTime) throws ExRatesNotPresentForBaseCurrencyException, ExRatesNotPresentAtTimeException;	
	
	public ExchangeAmount getExchangeAmount(String baseCurrency, String currency, BigDecimal baseCurrencyAmount) throws ExRatesNotPresentAtTimeException, ExRatesNotPresentForBaseCurrencyException ;

	public ByteArrayInputStream  getDownloadReport(String baseCurrency) throws IOException;	

}
