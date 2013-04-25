/**
 * 
 */
package benworks.comet.broadcast;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

/**
 * controller的抽象类
 * 
 * @author benworks
 */
public abstract class MultiGeneralController implements GeneralController {

	private final Map<String, Method> handlerMethodMap = new HashMap<String, Method>();

	public MultiGeneralController() {
		registerHandlerMethods();
	}

	public final GeneralResponse handle(GeneralRequest request)
			throws Exception {
		String methodName = getHandlerMethodName(request.getAction());
		Object returnValue = invokeMethod(methodName, request);
		if (returnValue instanceof GeneralResponse) {
			return (GeneralResponse) returnValue;
		} else {
			return null;
		}
	}

	private Object invokeMethod(String methodName, GeneralRequest request)
			throws Exception {
		Method method = this.handlerMethodMap.get(methodName);
		if (method == null) {
			throw new IllegalArgumentException("Not find handle method '"
					+ methodName + "' in " + getClass());
		}
		Object[] params = request.getParams();
		if (params == null)
			params = ArrayUtils.EMPTY_OBJECT_ARRAY;
		return method.invoke(this, params);
	}

	private final String getHandlerMethodName(String url) {
		int begin = url.lastIndexOf('/') + 1;
		return url.substring(begin);
	}

	/**
	 * 注册本Controller合法的方法
	 */
	private void registerHandlerMethods() {
		this.handlerMethodMap.clear();
		Method[] methods = getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (isHandlerMethod(method)) {
				this.handlerMethodMap.put(method.getName(), method);
			}
		}
	}

	/**
	 * 是否合法的方法
	 */
	private boolean isHandlerMethod(Method method) {
		if (method.getModifiers() == Modifier.PUBLIC)
			return false;
		Class<?> returnType = method.getReturnType();
		if (void.class.equals(returnType)
				|| returnType.isAssignableFrom(GeneralResponse.class)) {
			return true;
		}
		return false;
	}

}
