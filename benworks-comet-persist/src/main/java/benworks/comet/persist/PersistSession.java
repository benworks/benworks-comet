package benworks.comet.persist;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import benworks.comet.broadcast.MessageUtils;
import benworks.comet.common.logs.LogUtils;
import benworks.comet.common.utils.SpringUtils;
import benworks.comet.persist.jdbc.DynamicUpdate;

/**
 * 存储会话的管理
 * 
 * @author benworks
 * 
 */
public class PersistSession {

	private static DataSource[] dataSources;

	private static PlatformTransactionManager transactionManager = ServiceFactory
			.getTransactionManager();

	private static DefaultTransactionDefinition def = new DefaultTransactionDefinition();

	private static ThreadLocal<TransactionStatus> transactionStatusHolder = new ThreadLocal<TransactionStatus>();

	static {
		String[] dataSourceNames = SpringUtils
				.getBeanNamesOfType(DataSource.class);
		dataSources = new DataSource[dataSourceNames.length];
		for (int i = 0; i < dataSourceNames.length; i++) {
			dataSources[i] = SpringUtils.getBean(dataSourceNames[i],
					DataSource.class);
		}
		// 事务超时60s
		def.setTimeout(60);
	}

	public static void commit() {
		transactionCommit();
		DynamicUpdate.entityClean();
		LogUtils.commit();
		MessageUtils.commit();
	}

	public static void rollback() {
		LogUtils.rollback();
		MessageUtils.rollback();
		transactionRollback();
		DynamicUpdate.entityRollback();
	}

	public static void clean() {
		LogUtils.clear();
		MessageUtils.clear();
		transactionClean();
		DynamicUpdate.entityClean();
		connectionClose();

	}

	public static void transactionBegin() {
		TransactionStatus status = transactionStatusHolder.get();
		if (status != null) {
			return;
		}
		status = transactionManager.getTransaction(def);
		transactionStatusHolder.set(status);
	}

	private static void transactionCommit() {
		TransactionStatus status = transactionStatusHolder.get();
		if (status == null) {
			return;
		}
		transactionManager.commit(status);
		transactionClean();
	}

	private static void transactionRollback() {
		TransactionStatus status = transactionStatusHolder.get();
		if (status == null) {
			return;
		}
		transactionManager.rollback(status);
		transactionClean();
	}

	private static void transactionClean() {
		transactionStatusHolder.remove();
	}

	public static void connectionClose() {
		for (int i = 0; i < dataSources.length; i++) {
			try {
				if (TransactionSynchronizationManager
						.hasResource(dataSources[i])) {
					ConnectionHolder connectionHolder = (ConnectionHolder) TransactionSynchronizationManager
							.getResource(dataSources[i]);
					if (connectionHolder != null) {
						TransactionSynchronizationManager
								.unbindResource(dataSources[i]);
						JdbcUtils.closeConnection(connectionHolder
								.getConnection());
					}
				}
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void connectionOpen() {
		for (int i = 0; i < dataSources.length; i++) {
			if (!TransactionSynchronizationManager.hasResource(dataSources[i])) {
				ConnectionHandle handle = new LazyConnectionHandle(
						dataSources[i]);
				TransactionSynchronizationManager.bindResource(dataSources[i],
						new ConnectionHolder(handle));
			}
		}
	}

	/**
	 * 延迟获取数据库连接
	 * 
	 * @author benworks
	 * 
	 */
	static class LazyConnectionHandle implements ConnectionHandle {

		private DataSource ds;
		private Connection con;

		LazyConnectionHandle(DataSource ds) {
			this.ds = ds;
		}

		public Connection getConnection() {
			try {
				if (con == null)
					con = ds.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return con;
		}

		public void releaseConnection(Connection con) {

		}

	}
}
