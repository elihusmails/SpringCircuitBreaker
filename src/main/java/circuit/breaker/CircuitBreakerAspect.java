package circuit.breaker;

import java.util.concurrent.atomic.AtomicInteger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component("CircuitBreakerAspect")
public class CircuitBreakerAspect {

	private AtomicInteger failureCount;
	
	public CircuitBreakerAspect(){
		
		failureCount = new AtomicInteger();
	}
	
	@Before("execution(public * CircuitBreaker.invoke(..))")
	public void doBefore() throws Throwable {
		
	}
	
    @Around("execution(public * CircuitBreaker.invoke(..))")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("aspect Around started...");
        Object retVal = pjp.proceed();
        System.out.println("aspect Around ended...");
        return retVal;
    }

	
}
