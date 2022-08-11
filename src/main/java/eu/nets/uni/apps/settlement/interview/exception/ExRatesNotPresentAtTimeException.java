package eu.nets.uni.apps.settlement.interview.exception;

public class ExRatesNotPresentAtTimeException  extends Exception{

	private static final long serialVersionUID = 8507614542943090116L;

	public ExRatesNotPresentAtTimeException(String message) {
        super(message);
    }
}
