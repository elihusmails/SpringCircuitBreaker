package cb;

public class CircuitBreakerException extends Exception {

	private static final long serialVersionUID = 1L;

	public CircuitBreakerException() {
		super();
	}

	public CircuitBreakerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CircuitBreakerException(String arg0) {
		super(arg0);
	}

	public CircuitBreakerException(Throwable arg0) {
		super(arg0);
	}
}
