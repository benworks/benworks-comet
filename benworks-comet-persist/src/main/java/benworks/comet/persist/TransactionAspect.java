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
@Order(200)
public class TransactionAspect {

	// private final Log log = LogFactory.getLog(TransactionAspect.class);

	@Pointcut("execution(* benworks.tdgame..*Dao.save*(..)) || execution(* benworks.tdgame..*Dao.update*(..)) || execution(* benworks.tdgame..*Dao.delete*(..))")
	public void transactionPointcut() {

	}

	@Before("transactionPointcut()")
	public void beforeTransactionHandle() {
		// log.debug("#### beforeTransactionHandle");
		PersistSession.transactionBegin();
	}

	@AfterThrowing("transactionPointcut()")
	public void exceptionTransactionHandle() {
		// log.debug("#### exceptionTransactionHandle");
		// TransactionUtils.rollback();
	}
}
