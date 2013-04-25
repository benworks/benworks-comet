package benworks.comet.persist.jdbc;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import benworks.comet.persist.Persistable;

/**
 * 提供一个静态调用Cache的方法
 * 
 * @author benworks
 * 
 */
class CacheManager {

	private static Map<Class<? extends Persistable>, EntityCache<? extends Persistable>> cacheMap = new ConcurrentHashMap<Class<? extends Persistable>, EntityCache<? extends Persistable>>();

	public static EntityCache<Persistable> NULL_CACHE = new EntityCache<Persistable>(
			null) {

		public Persistable get(Serializable id) {
			return null;
		}

		public Class<Persistable> getClazz() {
			return Persistable.class;
		}

		public void remove(Serializable id) {

		}

		public void reset() {

		}

		public void set(Persistable t) {

		}

	};

	public static synchronized void reset() {
		cacheMap.clear();
	}

	@SuppressWarnings("unchecked")
	public static <T extends Persistable> EntityCache<T> getCache(Class<T> clazz) {
		TableInfo info = JdbcUtils.getTableInfo(clazz);
		if (!info.isCache())
			return (EntityCache<T>) NULL_CACHE;
		clazz = getParentClass(info, clazz);
		EntityCache<T> cache = (EntityCache<T>) cacheMap.get(clazz);
		if (cache == null) {
			cache = new EntityCache<T>(clazz);
			cacheMap.put(clazz, cache);
		}
		return cache;
	}

	public static Set<Class<? extends Persistable>> getCacheClasses() {
		return cacheMap.keySet();
	}

	@SuppressWarnings("unchecked")
	private static <T extends Persistable> Class<T> getParentClass(
			TableInfo info, Class<?> sun) {
		TableInfo parentInfo = info.getParent();
		while (parentInfo != null && parentInfo.getParent() != null)
			parentInfo = parentInfo.getParent();
		if (parentInfo == null)
			parentInfo = info;
		return (Class<T>) parentInfo.getClazz();
	}

}
