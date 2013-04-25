package benworks.comet.common.event;

/**
 * 事件派发器接口
 * 
 * @author benworks
 * 
 * @param <T>
 */
public interface Dispatchable<T extends GeneralEvent> {

	void addEventListener(Class<? extends T> eventType,
			GeneralEventListener<T> listener);

	boolean removeEventListener(Class<? extends T> eventType,
			GeneralEventListener<T> listener);

	void reset();

	boolean hasEventListener(Class<? extends T> eventType);

	boolean hasEventListener(Class<? extends T> eventType,
			GeneralEventListener<T> listener);

	boolean hasEventListener(Class<? extends T> eventType,
			Class<? extends GeneralEventListener<T>> listener);

	boolean dispatchEvent(T event);

}