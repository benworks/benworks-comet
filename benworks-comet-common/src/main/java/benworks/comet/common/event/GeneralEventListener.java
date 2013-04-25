package benworks.comet.common.event;

/**
 * 通用事件处理器
 * 
 * @author benworks
 * 
 * @param <T>
 */
public interface GeneralEventListener<T extends GeneralEvent> {

	void onEvent(T event);

}
