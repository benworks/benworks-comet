/**
 * 
 */
package benworks.comet.common.i18n;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

/**
 * @author benworks
 */
public class PickUpMessage {

	private static Map<String, List<String>> resources = new LinkedHashMap<String, List<String>>();

	public static void main(String[] args) throws Exception {
		String include = args[0];
		String filePath = args[1];
		scanClass(include);
		out(filePath);
	}

	private static void scanClass(String scanPackage) {
		try {
			String RESOURCE_PATTERN = "**/*.class";
			TypeFilter messageFilter = new AnnotationTypeFilter(Message.class,
					false);
			ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
			String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
					+ ClassUtils.convertClassNameToResourcePath(scanPackage)
					+ "/" + RESOURCE_PATTERN;
			Resource[] resources = resourcePatternResolver
					.getResources(pattern);
			MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(
					resourcePatternResolver);
			for (Resource resource : resources) {
				if (resource.isReadable()) {
					MetadataReader reader = readerFactory
							.getMetadataReader(resource);
					String className = reader.getClassMetadata().getClassName();
					// scan message
					if (messageFilter.match(reader, readerFactory)) {
						Class<?> clazz = resourcePatternResolver
								.getClassLoader().loadClass(className);
						// 类级别的信息
						classMessage(clazz);
						// 方法级别的信息
						methodMessage(clazz);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void classMessage(Class<?> clazz) {
		Message message = clazz.getAnnotation(Message.class);
		processMessage(clazz.getName(), message);
	}

	private static void methodMessage(Class<?> clazz) {
		Method[] methods = clazz.getDeclaredMethods();// clazz.getMethods();
		for (Method method : methods) {
			Message message = method.getAnnotation(Message.class);
			processMessage(clazz.getName(), message);
		}
	}

	private static void processMessage(String className, Message message) {
		if (message != null) {
			String[] values = message.value();
			String group = message.group();
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					String key = className + "." + group + "." + i;
					String value = values[i];
					String msg = key + " = " + value;
					System.out.println(msg);
					group(key, msg);
				}
			}

		}
	}

	private static void out(String filePath) {
		try {
			File file = new File(filePath);
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (!file.exists()) {
				file.createNewFile();
			}
			OutputStream writer = new FileOutputStream(file);
			System.out.println(file);
			List<String> keys = new ArrayList<String>(resources.keySet());
			Collections.sort(keys);
			int total = 0;
			for (String key : keys) {
				List<String> messages = resources.get(key);
				Collections.sort(messages);
				writer.write(("# group '" + key + "' size:" + messages.size() + "\n")
						.getBytes("utf-8"));
				total += messages.size();
			}
			writer.write(("# total:" + total + "\n\n").getBytes("utf-8"));
			for (String key : keys) {
				List<String> messages = resources.get(key);
				writer.write(("# '" + key + "' begin" + "\n").getBytes("utf-8"));
				for (String message : messages) {
					writer.write((message + "\n").getBytes("utf-8"));
				}
				writer.write(("# '" + key + "' end\n\n").getBytes("utf-8"));
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void group(String key, String message) {
		put(formKey(key), message);
	}

	private static String formKey(String key) {
		String[] packages = key.split("\\.");
		int prefixIndex = packages.length - 3;
		StringBuilder builder = new StringBuilder(64);
		for (int i = 0; i < prefixIndex && i < packages.length; i++) {
			builder.append(packages[i]);
			builder.append(".");
		}
		return builder.toString().substring(0, builder.length() - 1);
	}

	private static void put(String key, String message) {
		List<String> messages = resources.get(key);
		if (messages == null) {
			messages = new ArrayList<String>();
			resources.put(key, messages);
		}
		messages.add(message);
	}

}
