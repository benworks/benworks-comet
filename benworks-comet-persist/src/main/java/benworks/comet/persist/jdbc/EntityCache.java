/**
 * 
 */
package benworks.comet.persist.jdbc;

import java.io.Serializable;

import benworks.comet.persist.Persistable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 一个通用的实体Cache
 * 
 * @author benworks
 * 
 */
class EntityCache<T extends Persistable> {
	private Cache cache;

	private Class<T> clazz;

	public EntityCache(Class<T> clazz) {
		if (clazz == null)
			return;
		this.clazz = clazz;
		CacheManager cm = CacheManager.getInstance();
		cache = cm.getCache(clazz.getName());
		if (cache == null) {
			cm.addCache(clazz.getName());
			cache = cm.getCache(clazz.getName());
		}
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public void reset() {
		cache.removeAll();
	}

	public void remove(Serializable id) {
		cache.remove(id);
	}

	@SuppressWarnings("unchecked")
	public T get(Serializable id) {
		if (id == null)
			return null;
		Element element = cache.get(id);
		if (element != null)
			return (T) element.getValue();
		return null;
	}

	public void set(T t) {
		Element element = new Element(t.id(), t);
		cache.put(element);
	}

	public Cache getCache() {
		return cache;
	}
}
