package benworks.comet.persist;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import benworks.comet.common.model.Pageable;

/**
 * 通用DAO的接口
 * 
 * @author benworks
 */
public interface GenericDao {

	/**
	 * 获得符合查询条件的单个实体
	 * 
	 * @param clazz
	 *            对象类型
	 * @param sql
	 *            sql语句，不包含 select * from tableName
	 * @return sql语句的参数
	 */
	int count(Class<? extends Persistable> clazz, String sql, Object... args);

	/**
	 * 查询
	 * 
	 * @param pageIndex
	 *            当前页码，超过总页数，表示最后一页
	 * @param pageSize
	 *            每一页显示的条目数，当pageSize<=0时，表示取所有记录
	 * @param clazz
	 *            对象类型
	 * @param sql
	 *            sql语句，不包含 select * from tableName
	 * @param args
	 *            sql语句的参数
	 * @return Pageable
	 * @see Pageable
	 */
	<T extends Persistable> Pageable<T> find(int pageIndex, int pageSize,
			Class<T> clazz, String sql, Object... args);

	/**
	 * 获得指定class类型的的所有记录
	 * 
	 * @param clazz
	 *            持久化类类型
	 * @param maxCount
	 *            最大结果集
	 * @return 持久化类类型匹配的实体列表
	 */
	<T extends Persistable> List<T> getAll(Class<T> clazz, int maxCount);

	/**
	 * 获得指定查询条件的的所有记录
	 * 
	 * @param clazz
	 *            对象类型
	 * @param maxCount
	 *            最大结果集
	 * @param sql
	 *            sql语句，不包含 select * from tableName
	 * @param args
	 *            sql语句的参数
	 * @return 查询类型匹配的实体列表
	 */
	<T extends Persistable> List<T> getAll(Class<T> clazz, int maxCount,
			String sql, Object... args);

	/**
	 * 获得符合查询条件的单个实体
	 * 
	 * @param clazz
	 *            对象类型
	 * @param sql
	 *            sql语句，不包含 select * from tableName
	 * @param args
	 *            sql语句的参数
	 * @return
	 */
	<T extends Persistable> T get(Class<T> clazz, String sql, Object... args);

	/**
	 * 返回指定class类型指定主键的对象
	 * 
	 * @param clazz
	 *            持久化类类型
	 * @param id
	 *            主键
	 * @return 跟持久化类型匹配的实体或者null
	 */
	<T extends Persistable> T get(Class<T> clazz, Serializable id);

	/**
	 * 返回指定class类型指定主键的对象，此方法不从cache取数据
	 * 
	 * @param clazz
	 *            持久化类类型
	 * @param id
	 *            主键
	 * @return 跟持久化类型匹配的实体或者null
	 */
	<T extends Persistable> T getForUpdate(Class<T> clazz, Serializable id);

	/**
	 * 删除对象
	 * 
	 * @param 被删除的对象
	 * @return true－删除成功，false－失败
	 */
	boolean delete(Persistable t);

	/**
	 * 删除符合条件的对象
	 * 
	 * @param clazz
	 *            对象类型
	 * @param sql
	 *            sql语句，不包含 delete from tableName
	 * @param args
	 *            sql语句的参数
	 */
	boolean delete(Class<? extends Persistable> clazz, String sql,
			Object... args);

	/**
	 * 删除对象
	 * 
	 * @param clazz
	 *            对象类型
	 * @param pk
	 *            主键
	 * @return true－删除成功，false－失败
	 */
	boolean deleteInPk(Class<? extends Persistable> clazz, Serializable pk);

	/**
	 * 批量删除
	 * 
	 * @param 实体集合
	 */
	void deleteBatch(Collection<? extends Persistable> entities);

	/**
	 * 批量删除
	 * 
	 * @param 实体数组
	 */
	void deleteBatch(Persistable... entities);

	/**
	 * 保存对象
	 * 
	 * @param 要保存的对象
	 */
	void save(Persistable t);

	/**
	 * 批量保存
	 * 
	 * @param entities
	 *            操作的集合
	 */
	void saveBatch(Collection<? extends Persistable> entities);

	/**
	 * 批量保存
	 * 
	 * @param entities
	 *            操作的集合
	 */
	void saveBatch(Persistable... entities);

	/**
	 * 更新对象
	 * 
	 * @param 要更新的对象
	 */
	void update(Persistable t);

	/**
	 * 更新符合条件的对象
	 * 
	 * @param clazz
	 *            对象类型
	 * @param sql
	 *            sql语句，不包含 update tableName
	 * @param args
	 *            sql语句的参数
	 */
	void update(Class<? extends Persistable> clazz, String sql, Object... args);

	/**
	 * 批量更新
	 * 
	 * @param entities
	 *            操作的集合
	 */
	void updateBatch(Collection<? extends Persistable> entities);

	/**
	 * 批量更新
	 * 
	 * @param entities
	 *            操作的数组
	 */
	void updateBatch(Persistable... entities);

}