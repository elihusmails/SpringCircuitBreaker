package circuit.breaker.timer;


public interface CircuitBreakerTimerCallback extends Runnable {

	public void timeoutCallback(CircuitBreakerTimerStatus status);
	
}
