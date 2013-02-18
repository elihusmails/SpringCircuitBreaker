package cb;

import org.apache.log4j.Logger;

public class CircuitBreakerService implements ICircuitBreakerService
{
	private final Logger log = Logger.getLogger(CircuitBreakerService.class);
    
    public void withoutProblem()
    {
        log.info("without problems");
    }

    public void withProblem()
    {
         throw new BreakingException("breaking exception!");
    }

    public void withIgnoredProblem()
    {
         throw new IllegalArgumentException("illegal argument");
    }

	@Override
	public void doRealStuff() throws Exception {

		log.info("Doing real stuff");
	}
    
    public static class BreakingException extends RuntimeException
    {
		private static final long serialVersionUID = 1L;

		public BreakingException(final String s)
        {
            super(s);
        }
    }
}
