package circuit.breaker.timer;

import cb.CircuitBreakerStatus;

public class CircuitBreakerTimer implements Runnable {

	private CircuitBreakerTimerCallback callback;
	private Thread thread;
	private long sleepMs;
	
	public CircuitBreakerTimer( long sleepMs, CircuitBreakerTimerCallback callback ){

		this.sleepMs = sleepMs;
		this.callback = callback;
		thread = new Thread( this );
	}
	
	public void stop(){
		thread.interrupt();
		callback.timeoutCallback( CircuitBreakerTimerStatus.INTERRUPTED );
	}
	
	@Override
	public void run() {

		try {
			Thread.sleep(sleepMs * 1000);
		} catch (InterruptedException e) {
			callback.timeoutCallback( CircuitBreakerTimerStatus.INTERRUPTED );
			e.printStackTrace();
		}
		
		callback.timeoutCallback( CircuitBreakerTimerStatus.COMPLETED );
	}

}
