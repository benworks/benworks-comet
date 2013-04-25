/**
 * 
 */
package benworks.comet.persist;

import org.springframework.transaction.PlatformTransactionManager;

import benworks.comet.common.utils.SpringUtils;

/**
 * @author benworks
 * 
 */
class ServiceFactory {

	private static GenericDao genericDao;

	private static PlatformTransactionManager transactionManager;

	public static GenericDao getGenericDao() {
		if (genericDao == null) {
			genericDao = SpringUtils.getBean("genericDao", GenericDao.class);
		}
		return genericDao;
	}

	public static PlatformTransactionManager getTransactionManager() {
		if (transactionManager == null) {
			transactionManager = SpringUtils.getBean("transactionManager",
					PlatformTransactionManager.class);
		}
		return transactionManager;
	}

}
