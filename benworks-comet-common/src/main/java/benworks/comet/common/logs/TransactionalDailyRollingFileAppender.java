/**
 * 
 */
package benworks.comet.common.logs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 支持事务日志的DailyRollingFileAppender
 * 
 * @author benworks
 * 
 */
public class TransactionalDailyRollingFileAppender extends
		DailyRollingFileAppender implements TransactionalAppender {

	private ThreadLocal<List<LoggingEvent>> logs = new ThreadLocal<List<LoggingEvent>>();

	@Override
	protected void subAppend(LoggingEvent event) {
		List<LoggingEvent> tmpLogs = logs.get();
		if (tmpLogs == null) {
			tmpLogs = new ArrayList<LoggingEvent>();
			logs.set(tmpLogs);
		}
		tmpLogs.add(event);
		if (!LogUtils.hasAppender(this))
			LogUtils.addAppender(this);
	}

	/**
	 * 提交日志
	 */
	public void commit() {
		if (logs.get() == null) {
			return;
		}
		for (LoggingEvent evt : logs.get()) {
			super.subAppend(evt);
		}
	}

	/**
	 * 清空当前线程的日志对象
	 */
	public void rollback() {
		clear();
	}

	/**
	 * 清空当前线程的日志对象
	 */
	public void clear() {
		List<LoggingEvent> tmpLogs = logs.get();
		if (tmpLogs == null)
			return;
		tmpLogs.clear();
	}
}
