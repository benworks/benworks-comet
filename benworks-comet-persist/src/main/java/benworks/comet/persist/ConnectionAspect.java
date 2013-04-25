package benworks.comet.persist;

import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

/**
 * @author benworks
 * 
 */
@Aspect
@Order(100)
public class ConnectionAspect {

	// private final Log log = LogFactory.getLog(ConnectionAspect.class);

	@Pointcut("execution(* benworks.tdgame..*Dao.*(..))")
	public void connectionPointcut() {

	}

	@Before("connectionPointcut()")
	public void beforeConnectionHandle() {
		// log.debug("***** beforeConnectionHandle");
		PersistSession.connectionOpen();
	}

	@AfterThrowing("connectionPointcut()")
	public void exceptionConnectionHandle() {
		// log.debug("***** exceptionConnectionHandle");
		// ConnectionUtils.close();
	}
}
