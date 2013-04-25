package benworks.comet.common.event;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import benworks.comet.common.model.OrderComparator;

/**
 * 时间派发器的默认实现
 * 
 * @author benworks
 * 
 * @param <T>
 */
public class EventDispatcher<T extends GeneralEvent> implements Dispatchable<T> {

	protected Map<Class<? extends T>, List<GeneralEventListener<T>>> dispatcher;

	public void addEventListener(Class<? extends T> eventType,
			GeneralEventListener<T> listener) {
		if (eventType == null || listener == null)
			return;
		if (dispatcher == null) {
			dispatcher = new ConcurrentHashMap<Class<? extends T>, List<GeneralEventListener<T>>>();
		}
		List<GeneralEventListener<T>> listeners = dispatcher.get(eventType);
		if (listeners == null) {
			listeners = new CopyOnWriteArrayList<GeneralEventListener<T>>();
			dispatcher.put(eventType, listeners);
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
			Collections.sort(listeners, OrderComparator.getInstance());
		}
	}

	public boolean removeEventListener(Class<? extends T> eventType,
			GeneralEventListener<T> listener) {
		if (eventType == null || listener == null)
			return false;
		if (dispatcher == null) {
			return false;
		}
		List<GeneralEventListener<T>> listeners = dispatcher.get(eventType);
		if (listeners == null)
			return false;
		return listeners.remove(listener);
	}

	public void reset() {
		if (dispatcher != null)
			dispatcher.clear();
	}

	public boolean hasEventListener(Class<? extends T> eventType) {
		if (eventType == null)
			return false;
		if (dispatcher == null) {
			return false;
		}
		List<GeneralEventListener<T>> listeners = dispatcher.get(eventType);
		if (listeners == null)
			return false;
		return listeners.size() > 0;
	}

	public boolean hasEventListener(Class<? extends T> eventType,
			GeneralEventListener<T> listener) {
		if (eventType == null)
			return false;
		if (dispatcher == null) {
			return false;
		}
		List<GeneralEventListener<T>> listeners = dispatcher.get(eventType);
		if (listeners == null)
			return false;
		return listeners.contains(listener);
	}

	public boolean hasEventListener(Class<? extends T> eventType,
			Class<? extends GeneralEventListener<T>> listener) {
		if (eventType == null)
			return false;
		if (dispatcher == null) {
			return false;
		}
		List<GeneralEventListener<T>> listeners = dispatcher.get(eventType);
		if (listeners == null)
			return false;
		for (GeneralEventListener<T> instance : listeners) {
			if (listener.isAssignableFrom(instance.getClass())) {
				return true;
			}
		}
		return false;
	}

	public boolean dispatchEvent(T event) {
		if (dispatcher == null)
			return false;
		List<GeneralEventListener<T>> listeners = dispatcher.get(event
				.getClass());
		if (listeners == null)
			return false;
		for (GeneralEventListener<T> listener : listeners) {
			listener.onEvent(event);
		}
		return true;
	}
}
