package eu.nets.uni.apps.settlement.interview.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExchangeRatesExceptionHandler {


	    @ResponseStatus(HttpStatus.NOT_FOUND)
	    @ExceptionHandler(ExRatesNotPresentForBaseCurrencyException.class)
	    public Map<String, String> handleBusinessException(ExRatesNotPresentForBaseCurrencyException ex) {
	        Map<String, String> errorMap = new HashMap<>();
	        errorMap.put("errorMessage", ex.getMessage());
	        return errorMap;
	    }
	    
	    @ResponseStatus(HttpStatus.NOT_FOUND)
	    @ExceptionHandler(ExRatesNotPresentAtTimeException.class)
	    public Map<String, String> handleBusinessException(ExRatesNotPresentAtTimeException ex) {
	        Map<String, String> errorMap = new HashMap<>();
	        errorMap.put("errorMessage", ex.getMessage());
	        return errorMap;
	    }
	    
	   
	    
}
