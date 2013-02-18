package cb;

import org.aspectj.lang.annotation.Aspect;

@Aspect
public interface ICircuitBreakerService
{
    public static final String BREAKER_ID = "MY_BREAKER";

    @UseCircuitBreaker(BREAKER_ID)
    public void withoutProblem();

    @UseCircuitBreaker(BREAKER_ID)
    public void withProblem();

    @UseCircuitBreaker(BREAKER_ID)
    public void withIgnoredProblem();
    
    @UseCircuitBreaker(BREAKER_ID)
    public void doRealStuff() throws Exception;
}