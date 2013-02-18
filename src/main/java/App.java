import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cb.ICircuitBreakerService;

public class App {

	private static final Logger log = Logger.getLogger(App.class);
	
    public static void main(String[] args) throws InterruptedException {
    	
    	File file = new File("spring.xml");
        ApplicationContext context = new ClassPathXmlApplicationContext(file.getName());

        ICircuitBreakerService service = (ICircuitBreakerService)context.getBean("circuitBreakerService");
        service.withoutProblem();
        
        for( int i=0; i<4; i++ ){
        	try{
        		service.withProblem();
        	}catch(Exception e){
        		///System.out.println( e.getMessage() );
        	}
        }
        
        try {
        	Thread.sleep(5000);
			service.doRealStuff();
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        log.info("Sleeping...");
        Thread.sleep(10*1000);
    }
}
