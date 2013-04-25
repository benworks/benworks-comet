package benworks.comet.common.logs;

public interface TransactionalAppender {

	/**
	 * 提交日志
	 */
	public void commit();

	/**
	 * 清空当前线程的日志对象
	 */
	public void rollback();

	/**
	 * 清空当前线程的日志对象
	 */
	public void clear();
}
