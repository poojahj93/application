package eu.nets.uni.apps.settlement.interview.exception;

public class ExRatesNotPresentForBaseCurrencyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8507614542943090116L;

	public ExRatesNotPresentForBaseCurrencyException(String message) {
        super(message);
    }
}