package benworks.comet.broadcast;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 路径与Controller的映射关系
 * 
 * @author benworks
 */
@Service
public class ClassNameHandlerMapping {

	private Log log = LogFactory.getLog(ClassNameHandlerMapping.class);

	private final static String CONTROLLER_SUFFIX = "Controller";

	@Autowired
	private Set<GeneralController> controllers;

	private Map<String, GeneralController> controllerMap;

	@PostConstruct
	public void init() {
		controllerMap = new ConcurrentHashMap<String, GeneralController>(
				controllers.size() + 10);
		for (GeneralController controller : controllers) {
			String urlPath = generatePathMapping(controller.getClass());
			registerController(urlPath, controller);
		}
	}

	public GeneralController getController(String key) {
		return controllerMap.get(key);
	}

	public void unRegisterController(String key) {
		controllerMap.remove(key);
	}

	public void registerController(String key, GeneralController controller) {
		log.info("Registering Controller \'" + key
				+ "\' as handler for URL path [" + key + "]");
		Object mappedHandler = this.controllerMap.get(key);
		if (mappedHandler != null) {
			throw new IllegalStateException("Cannot map handler ["
					+ controller.getClass() + "] to URL path [" + key
					+ "]: There is already handler [" + mappedHandler
					+ "] mapped.");
		}

		this.controllerMap.put(key, controller);
		log.info("Mapped URL path [" + key + "] onto handler ["
				+ controller.getClass() + "]");
	}

	private String generatePathMapping(Class<?> beanClass) {
		String className = ClassUtils.getShortClassName(beanClass.getName());
		String path = className.endsWith(CONTROLLER_SUFFIX) ? className
				.substring(0, className.indexOf(CONTROLLER_SUFFIX)) : className;
		return path.toLowerCase();
	}
}
