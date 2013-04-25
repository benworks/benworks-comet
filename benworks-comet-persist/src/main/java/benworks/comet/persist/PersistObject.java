/**
 * 
 */
package benworks.comet.persist;

import java.io.Serializable;
import java.util.List;

import benworks.comet.persist.jdbc.JdbcPaginaction;

/**
 * 可存储的模型的父类.
 * 
 * @author benworks
 */
public abstract class PersistObject implements Persistable {

	private static final long serialVersionUID = 7616983668248729012L;

	/**
	 * 为所有对象提供一个公共的访问主键的方法
	 * 
	 * @return
	 */
	public abstract Serializable id();

	public static <T extends Persistable> T get(Class<T> clazz, Serializable id) {
		return ServiceFactory.getGenericDao().get(clazz, id);
	}

	public static <T extends Persistable> List<T> gets(Class<T> clazz) {
		return ServiceFactory.getGenericDao().getAll(clazz,
				JdbcPaginaction.MAX_RESULT_SIZE);
	}

	public void update() {
		ServiceFactory.getGenericDao().update(this);
		afterUpdate();
	}

	public void delete() {
		ServiceFactory.getGenericDao().delete(this);
		afterDelete();
	}

	public static <T extends Persistable> void deleteInPk(Class<T> clazz,
			Serializable id) {
		ServiceFactory.getGenericDao().deleteInPk(clazz, id);
	}

	public void save() {
		ServiceFactory.getGenericDao().save(this);
		afterSave();
	}

	protected void afterSave() {

	}

	protected void afterUpdate() {

	}

	protected void afterDelete() {

	}
}
