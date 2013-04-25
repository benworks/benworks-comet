/**
 * 
 */
package benworks.comet.common.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * 可以获得所有用srping配置的类
 */
@Service
public class SpringUtils implements ApplicationContextAware {

	private static ApplicationContext applicationContext; // Spring应用上下文环境

	private static Map<Class<?>, Object> typeMap = new ConcurrentHashMap<Class<?>, Object>();

	/**
	 * 实现ApplicationContextAware接口的回调方法，设置上下文环境
	 * 
	 * @param applicationContext
	 */
	public void setApplicationContext(
			final ApplicationContext applicationContext) {
		SpringUtils.applicationContext = applicationContext;
	}

	/**
	 * @return ApplicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 获取对象
	 * 
	 * @param name
	 * @return Object 一个在spring以所给名字注册的bean的实例
	 */
	public static Object getBean(final String name) {
		if (applicationContext == null) {
			return null;
		}
		return applicationContext.getBean(name);
	}

	/**
	 * 获取类型为requiredType的对象
	 * 如果bean不能被类型转换，相应的异常将会被抛出（BeanNotOfRequiredTypeException）
	 * 
	 * @param name
	 *            bean注册名
	 * @param requiredType
	 *            返回对象类型
	 * @return Object 一个在spring以所给名字注册的bean的实例，返回必须是requiredType类型对象
	 */
	public static <T> T getBean(final String name, final Class<T> requiredType) {
		if (applicationContext == null) {
			return null;
		}
		return (T) applicationContext.getBean(name, requiredType);
	}

	/**
	 * 获取类型为type的所有在spring注册的对象名字
	 * 
	 * @param type
	 *            对象类型
	 * @return String[] 对象名字列表
	 */
	public static String[] getBeanNamesOfType(final Class<?> type) {
		if (applicationContext == null) {
			return new String[0];
		}
		return applicationContext.getBeanNamesForType(type);
	}

	/**
	 * 获取类型为type的在spring注册的对象，多个返回第一个<br>
	 * 这里出于效率的考虑，会做些缓存
	 * 
	 * @param <T>
	 * @param type
	 *            对象类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBeanOfType(final Class<T> type) {
		if (applicationContext == null) {
			return null;
		}
		Object bean = typeMap.get(type);
		if (bean != null)
			return (T) bean;
		else {
			String[] names = applicationContext.getBeanNamesForType(type);
			if (names == null || names.length == 0)
				return null;
			T t = getBean(names[0], type);
			typeMap.put(type, t);
			return t;
		}
	}

}
