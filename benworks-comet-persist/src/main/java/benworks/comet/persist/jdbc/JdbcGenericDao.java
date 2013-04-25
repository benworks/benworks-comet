/**
 * Xlands-ihome project 2005
 */
package benworks.comet.persist.jdbc;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import benworks.comet.common.model.Pageable;
import benworks.comet.common.model.SimplePaginaction;
import benworks.comet.persist.GenericDao;
import benworks.comet.persist.Persistable;

/**
 * 通用DAO实现
 * 
 * @author benworks
 */
@Repository("genericDao")
public class JdbcGenericDao extends SimpleJdbcDaoSupport implements GenericDao {

	public int count(Class<? extends Persistable> clazz, String sql,
			Object... args) {
		TableInfo tableInfo = JdbcUtils.getTableInfo(clazz);
		return getSimpleJdbcTemplate().queryForInt(
				tableInfo.queryCountByCondition(sql), args);
	}

	public <T extends Persistable> Pageable<T> find(int pageIndex,
			int pageSize, Class<T> clazz, String sql, Object... args) {
		return new JdbcPaginaction<T>(getSimpleJdbcTemplate(), pageIndex,
				pageSize, clazz, sql, args);
	}

	public <T extends Persistable> List<T> getAll(Class<T> clazz, int maxCount) {
		return getAll(clazz, maxCount, "");
	}

	public <T extends Persistable> List<T> getAll(Class<T> clazz, int maxCount,
			String sql, Object... args) {
		if (maxCount < 0 || maxCount > SimplePaginaction.MAX_RESULT_SIZE) {
			maxCount = SimplePaginaction.MAX_RESULT_SIZE;
		}
		TableInfo tableInfo = JdbcUtils.getTableInfo(clazz);
		String newsql = tableInfo.queryByCondition(sql, false) + " limit ?";
		Object[] newargs = args;
		if (args == null || args.length == 0) {
			newargs = new Object[] { maxCount };
		} else {
			int length = args.length;
			newargs = new Object[length + 1];
			System.arraycopy(args, 0, newargs, 0, length);
			newargs[length] = maxCount;
		}
		List<T> list = getSimpleJdbcTemplate().query(newsql,
				JdbcUtils.getRowMapper(clazz), newargs);
		// 有ListIgnore标记和父子表结构的实体不进行缓存
		if (tableInfo.isCache() && !tableInfo.isHasListIgnore()
				&& !tableInfo.isHasSubTable()) {
			for (T t : list)
				CacheManager.getCache(clazz).set(t);
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public <T extends Persistable> T get(Class<T> clazz, String sql,
			Object... args) {
		TableInfo tableInfo = JdbcUtils.getTableInfo(clazz);
		try {
			T t = getSimpleJdbcTemplate().queryForObject(
					tableInfo.queryByCondition(sql, true),
					JdbcUtils.getRowMapper(clazz), args);
			TableInfo tableInfo2 = JdbcUtils.getTableInfo(t.getClass());
			if (!tableInfo2.getTableName().equals(tableInfo.getTableName()))
				t = (T) get(t.getClass(), t.id());
			else {
				if (tableInfo.isCache())
					CacheManager.getCache(clazz).set(t);
			}
			DynamicUpdate.addEntity(t);
			return t;
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
	}

	public <T extends Persistable> T get(Class<T> clazz, Serializable id) {
		T t = (T) CacheManager.getCache(clazz).get(id);
		if (t == null)
			t = getForUpdate(clazz, id, false);
		DynamicUpdate.addEntity(t);
		return t;
	}

	public <T extends Persistable> T getForUpdate(Class<T> clazz,
			Serializable id) {
		T t = getForUpdate(clazz, id, true);
		DynamicUpdate.addEntity(t);
		return t;
	}

	@SuppressWarnings("unchecked")
	private <T extends Persistable> T getForUpdate(Class<T> clazz,
			Serializable id, boolean update) {
		TableInfo tableInfo = JdbcUtils.getTableInfo(clazz);
		try {
			T t = getSimpleJdbcTemplate().queryForObject(
					tableInfo.queryByPk(id, update),
					JdbcUtils.getRowMapper(clazz), id);
			TableInfo tableInfo2 = JdbcUtils.getTableInfo(t.getClass());
			if (!tableInfo2.getTableName().equals(tableInfo.getTableName()))
				t = (T) get(t.getClass(), t.id());
			if (tableInfo.isCache())
				CacheManager.getCache(clazz).set(t);
			return t;
		} catch (Exception e) {
			return null;
		}
	}

	public boolean delete(Persistable obj) {
		TableInfo tableInfo = JdbcUtils.getTableInfo(obj.getClass());
		CacheManager.getCache(obj.getClass()).remove(obj.id());
		return getSimpleJdbcTemplate().update(tableInfo.deleteByPk(obj.id()),
				obj.id()) > 0;
	}

	public boolean delete(Class<? extends Persistable> clazz, String sql,
			Object... args) {
		TableInfo tableInfo = JdbcUtils.getTableInfo(clazz);
		return getSimpleJdbcTemplate().update(tableInfo.deleteByCondition(sql),
				args) >= 0;
	}

	public boolean deleteInPk(Class<? extends Persistable> clazz,
			Serializable pk) {
		TableInfo tableInfo = JdbcUtils.getTableInfo(clazz);
		CacheManager.getCache(clazz).remove(pk);
		return getSimpleJdbcTemplate().update(tableInfo.deleteByPk(pk), pk) > 0;
	}

	public void deleteBatch(Collection<? extends Persistable> entities) {
		for (Persistable Persistable : entities) {
			delete(Persistable);
		}
	}

	public void deleteBatch(Persistable... entities) {
		for (Persistable Persistable : entities) {
			delete(Persistable);
		}
	}

	public void save(Persistable t) {
		SimpleJdbcInsert insert = JdbcUtils.getJdbcInsert(t.getClass(),
				getDataSource());
		insert.execute(new InheritBeanPropertySqlParameterSource(t));
	}

	public void saveBatch(Collection<? extends Persistable> entities) {
		for (Persistable Persistable : entities) {
			save(Persistable);
		}
	}

	public void saveBatch(Persistable... entities) {
		for (Persistable Persistable : entities) {
			save(Persistable);
		}
	}

	public void update(Persistable t) {
		TableInfo tableInfo = JdbcUtils.getTableInfo(t.getClass());
		String sql = tableInfo.updateByPk(t);
		if (StringUtils.isEmpty(sql))
			return;
		CacheManager.getCache(t.getClass()).remove(t.id());
		DynamicUpdate.removeEntity(t);
		getSimpleJdbcTemplate().update(sql,
				new BeanPropertySqlParameterSource(t));
	}

	public void update(Class<? extends Persistable> clazz, String sql,
			Object... args) {
		TableInfo tableInfo = JdbcUtils.getTableInfo(clazz);
		getSimpleJdbcTemplate().update(tableInfo.updateByCondition(sql), args);
	}

	public void updateBatch(Collection<? extends Persistable> entities) {
		for (Persistable Persistable : entities) {
			update(Persistable);
		}
	}

	public void updateBatch(Persistable... entities) {
		for (Persistable Persistable : entities) {
			update(Persistable);
		}
	}
}
