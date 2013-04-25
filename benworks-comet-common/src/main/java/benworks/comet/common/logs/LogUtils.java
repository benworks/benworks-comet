/**
 * 
 */
package benworks.comet.common.logs;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import benworks.comet.common.utils.RandomUtils;

/**
 * 事务日志工具类
 * 
 * @author benworks
 * 
 */
public class LogUtils {
	private static ThreadLocal<List<TransactionalAppender>> transactionalAppends = new ThreadLocal<List<TransactionalAppender>>();
	private static ThreadLocal<String> identityLocalThreads = new ThreadLocal<String>();

	public static String getLogTransactionalIdentity() {
		String id = identityLocalThreads.get();
		if (id == null) {
			id = System.currentTimeMillis() + "-" + RandomUtils.nextInt(1000);
			identityLocalThreads.set(id);
		}
		return id;
	}

	public static void log(String logName, GeneralLog tdLog) {
		Log log = LogFactory.getLog(logName);
		log.info(tdLog);
	}

	/**
	 * 当前线程事务日志提交 对当前线程的日志进行写入
	 */
	public static void commit() {
		List<TransactionalAppender> list = getAppenders();
		if (list == null)
			return;
		for (TransactionalAppender appender : list)
			appender.commit();
		LogUtils.clear();
	}

	/**
	 * 当前线程事务日志回滚 清空当前线程日志，以避免内存溢出
	 */
	public static void rollback() {
		String identity = identityLocalThreads.get();
		if (identity != null)
			identityLocalThreads.set(null);
		List<TransactionalAppender> list = getAppenders();
		if (list == null)
			return;
		for (TransactionalAppender appender : list)
			appender.rollback();
		list.clear();
	}

	public static void clear() {
		String identity = identityLocalThreads.get();
		if (identity != null)
			identityLocalThreads.set(null);
		List<TransactionalAppender> list = getAppenders();
		if (list == null)
			return;
		for (TransactionalAppender appender : list)
			appender.clear();
		list.clear();
	}

	/**
	 * 当前线程增加Appender
	 * 
	 * @param appender
	 */
	static void addAppender(TransactionalAppender appender) {
		List<TransactionalAppender> list = getAppenders();
		if (list == null) {
			list = new ArrayList<TransactionalAppender>();
			transactionalAppends.set(list);
		}
		list.add(appender);
	}

	/**
	 * 当前线程删除Appender
	 * 
	 * @param appender
	 */
	static boolean hasAppender(TransactionalAppender appender) {
		List<TransactionalAppender> list = getAppenders();
		if (list != null) {
			return list.contains(appender);
		}
		return false;
	}

	/**
	 * 当前线程删除Appender
	 * 
	 * @param appender
	 */
	static void removeAppender(TransactionalAppender appender) {
		List<TransactionalAppender> list = getAppenders();
		if (list != null) {
			list.remove(appender);
		}
	}

	static List<TransactionalAppender> getAppenders() {
		return transactionalAppends.get();
	}
}
