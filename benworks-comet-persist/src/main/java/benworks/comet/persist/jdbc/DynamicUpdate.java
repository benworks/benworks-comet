package benworks.comet.persist.jdbc;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.BeanUtils;

import benworks.comet.persist.Persistable;

public class DynamicUpdate {

	private static ThreadLocal<Map<String, OriginalEntity>> entityThreadLocal = new ThreadLocal<Map<String, OriginalEntity>>();

	public static void addEntity(Persistable entity) {
		if (entity == null)
			return;
		TableInfo info = JdbcUtils.getTableInfo(entity.getClass());
		if (info == null)
			return;
		if (info.isImmutable())
			return;
		Map<String, OriginalEntity> map = entityThreadLocal.get();
		if (map == null) {
			map = new HashMap<String, OriginalEntity>();
			entityThreadLocal.set(map);
		}
		String key = entityKey(entity);
		if (!map.containsKey(key)) {
			map.put(key, new OriginalEntity(entity));
		}

	}

	public static void removeEntity(Persistable entity) {
		if (entity == null)
			return;
		Map<String, OriginalEntity> map = entityThreadLocal.get();
		if (map == null)
			return;
		String key = entityKey(entity);
		if (map.containsKey(key))
			map.remove(key);
	}

	public static String[] getUpdateProperties(Persistable entity) {
		Map<String, OriginalEntity> map = entityThreadLocal.get();
		if (map == null)
			return null;
		OriginalEntity oentity = map.get(entityKey(entity));
		if (oentity == null)
			return null;
		List<String> list = oentity.getUpdateProperties(entity);
		return list.toArray(new String[list.size()]);
	}

	private static String entityKey(Persistable entity) {
		return entity.getClass().getName() + "-" + entity.id();
	}

	public static void entityRollback() {
		Map<String, OriginalEntity> map = entityThreadLocal.get();
		if (map != null) {
			for (OriginalEntity oentity : map.values()) {
				CacheManager.getCache(oentity.getEntity().getClass()).remove(
						oentity.getEntity().id());
			}
		}
		entityClean();
	}

	public static void entityClean() {
		Map<String, OriginalEntity> map = entityThreadLocal.get();
		if (map != null)
			map.clear();
		entityThreadLocal.set(null);
	}

	/**
	 * 未修改前对象的一个属性副本，以便做动态更新
	 * 
	 * @author benworks
	 * 
	 */
	static class OriginalEntity {
		private Persistable entity;
		private Map<String, Object> map;

		OriginalEntity(Persistable entity) {
			this.entity = entity;
			this.map = copyProperties(entity);
		}

		private Map<String, Object> copyProperties(Persistable entity) {
			Map<String, Object> map = new HashMap<String, Object>();
			String[] properties = JdbcUtils.getTableInfo(entity.getClass())
					.getUpdateProperties();
			for (String property : properties) {
				PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(
						entity.getClass(), property);
				if (pd == null)
					continue;
				try {
					Method readMethod = pd.getReadMethod();
					Object value = readMethod.invoke(entity, new Object[0]);
					map.put(pd.getName(), value);
				} catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
			return map;
		}

		Persistable getEntity() {
			return entity;
		}

		Map<String, Object> getMap() {
			return map;
		}

		boolean isDirty() {
			// TODO 优化比较顺序可以提交效率
			if (map == null)
				return false;
			for (String property : map.keySet()) {
				PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(
						entity.getClass(), property);
				if (pd == null)
					return false;
				Object obj = null;
				try {
					obj = pd.getReadMethod().invoke(entity);
				} catch (Exception e) {
					return false;
				}

				Object originalValue = map.get(property);
				if (originalValue == null && obj != null) {
					return true;
				}
				if (originalValue != null && !originalValue.equals(obj)) {
					return true;
				}
			}
			return false;
		}

		List<String> getUpdateProperties(Persistable entity) {
			List<String> needUpdateProperties = new ArrayList<String>();
			for (Entry<String, Object> entry : map.entrySet()) {
				String property = entry.getKey();
				PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(
						entity.getClass(), property);
				if (pd == null)
					continue;
				Object obj = null;
				try {
					obj = pd.getReadMethod().invoke(entity);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Object originalValue = entry.getValue();
				if (originalValue == obj)
					continue;
				if (originalValue == null && obj != null) {
					needUpdateProperties.add(property);
				}
				if (originalValue != null && !originalValue.equals(obj)) {
					needUpdateProperties.add(property);
				}
			}
			return needUpdateProperties;
		}
	}
}
