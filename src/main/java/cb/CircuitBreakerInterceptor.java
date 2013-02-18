package cb;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

public class CircuitBreakerInterceptor implements MethodInterceptor {
	
	private final Logger log = Logger.getLogger(CircuitBreakerInterceptor.class);
	
	private Map<String, CircuitBreaker> breakers = new ConcurrentHashMap<String, CircuitBreaker>();
	private Set<Class<? extends Throwable>> nonTrippingExceptions;
	private final int failureThreshold;
	private final int timeUntilRetry;

	public CircuitBreakerInterceptor(int failureThreshold, int timeUntilRetry) {
		this.failureThreshold = failureThreshold;
		this.timeUntilRetry = timeUntilRetry;
		nonTrippingExceptions = new CopyOnWriteArraySet<Class<? extends Throwable>>();
	}

	public CircuitBreaker getBreaker(String name) {
		if (breakers.get(name) == null) {
			breakers.put(name, new CircuitBreaker(name, failureThreshold,
					timeUntilRetry));
		}
		return breakers.get(name);
	}

	private boolean isNonTrippingException(Throwable t) {
		for (Class<? extends Throwable> throwable : nonTrippingExceptions) {
			if (throwable.isAssignableFrom(t.getClass())) {
				return true;
			}
		}
		return false;
	}

	public Set<Class<? extends Throwable>> getNonTrippingExceptions() {
		return nonTrippingExceptions;
	}

	public void setNonTrippingExceptions(
			final Set<Class<? extends Throwable>> nonTrippingExceptions) {
		this.nonTrippingExceptions = nonTrippingExceptions;
	}

	public Object invoke(final MethodInvocation invocation) throws Throwable {
		//log.info("Going through circuitbreaker protected method");
		
		// get the annotation for the method
		UseCircuitBreaker breakerAnnot = invocation.getMethod().getAnnotation(UseCircuitBreaker.class);
		if (breakerAnnot != null) {
			
			// get (or create) a circuit breaker based on the annotation value
			CircuitBreaker breaker = getBreaker(breakerAnnot.value());
			Object returnValue = null;
			if (breaker.isOpen()) {
				if (breaker.isWaitTimeExceeded()) {
					try {
						breaker.openHalf();
						log.info("Retrying operation " + invocation.getMethod().toGenericString()
								+ " after waitTime exceeded");
						returnValue = invocation.proceed();
						log.info("Retry of operation " + invocation.getMethod().toGenericString()
								+ " succeeded, resetting circuit breaker");
						breaker.close();
					} catch (Throwable t) {
						if (isNonTrippingException(t)) {
							log.info( "Retry of operation " + invocation.getMethod().toGenericString()
											+ " succeeded (but threw legal exception), resetting circuit breaker");
							breaker.close();
							throw t;
						}
						log.error("Retry of operation " + invocation.getMethod().toGenericString()
								+ " failed, circuit breaker still closed.", t);
						breaker.open();
						throw new CircuitBreakerException(
								"The operation " + invocation.getMethod().toGenericString()
										+ " has too many failures, tripping circuit breaker.");
					}
				} else {
					// if wait time has not been exceeded
					throw new CircuitBreakerException(
							"This operation cannot be performed due to open circuit breaker (too many failures).");
				}
			} else if (breaker.isClosed()) {
				try {
					returnValue = invocation.proceed();
					breaker.close();
				} catch (Throwable t) {
					log.info("Failure of operation " + invocation.getMethod().toGenericString()
							+ " in circuit breaker");
					if (!isNonTrippingException(t)) {
						breaker.addFailure();
						if (breaker.isThresholdReached()) {
							log.error("Circuit breaker tripped on operation "
									+ invocation.getMethod().toGenericString()
									+ " failure.");
							breaker.open();
							throw new CircuitBreakerException("The operation "
											+ invocation.getMethod().toGenericString()
											+ " has too many failures, tripping circuit breaker.");
						} else {
							throw t;
						}
					} else {
						throw t;
					}
				}
			} else if (breaker.isHalfOpen()) {
				throw new CircuitBreakerException("Busy retrying operation in opened circuit breaker");
			}
			return returnValue;
		} else {
			return invocation.proceed();
		}
	}
}