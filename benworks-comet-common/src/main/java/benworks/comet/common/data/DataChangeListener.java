package benworks.comet.common.data;

import java.util.Map;

/**
 * 
 * @author benworks
 * 
 */
public interface DataChangeListener {

	/**
	 * 关注的数据类型
	 * 
	 * @return
	 */
	Class<?>[] interestClass();

	/**
	 * 当关注的数据类型发生变化触发的动作
	 * 
	 * @param clazz
	 *            数据类型
	 * @param dataMap
	 *            新的数据
	 */
	void change(Class<?> clazz, Map<Integer, Object> dataMap);

}
