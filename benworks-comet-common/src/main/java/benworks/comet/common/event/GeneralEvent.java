package benworks.comet.common.event;

/**
 * 通用事件模型
 * 
 * @author benworks
 * 
 */
public interface GeneralEvent {

	/**
	 * 被listener处理过的次数
	 * 
	 * @return
	 */
	int getProcessCount();

	void setProcessCount(int processCount);

}
