package circuit.breaker;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreaker<T> {

	private static final int DEFAULT_POOL_SIZE = 10;
	
	private AtomicInteger failureCount;
	private ExecutorService executor;
	
	public CircuitBreaker(){
	this( DEFAULT_POOL_SIZE );	
	}
	
	public CircuitBreaker( int poolSize ){
	
		executor = Executors.newFixedThreadPool(10);
		failureCount = new AtomicInteger();
	}

	public void increaseFailureCount() {

		failureCount.incrementAndGet();
	}
	
	public Future<T> invoke( Callable<T> task ){
		try{
			Future<T> result = executor.submit(task);
			return result;
		}catch(Exception e){
			e.printStackTrace();
			increaseFailureCount();
		}
		
		return null;
	}
}
