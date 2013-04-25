/**
 * 
 */
package benworks.comet.persist;

import java.io.Serializable;

/**
 * 可存储的模型接口定义
 * 
 * @author benworks
 */
public interface Persistable extends Serializable {

	/**
	 * 为所有对象提供一个公共的访问主键的方法
	 * 
	 * @return
	 */
	Serializable id();

}
