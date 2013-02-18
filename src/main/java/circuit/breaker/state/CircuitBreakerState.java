package circuit.breaker.state;

import circuit.breaker.CircuitBreaker;

public class CircuitBreakerState {

	private CircuitBreaker breaker;
	
	public CircuitBreakerState( CircuitBreaker breaker ){
		
		this.breaker = breaker;
	}
	
	public void ProtectedCodeIsAboutToBeCalled() { }
    public void ProtectedCodeHasBeenCalled() { }
    public void ActUponException(Exception e) { breaker.increaseFailureCount(); }
}
