package benworks.comet.common.i18n;

import java.io.File;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MessageBundle {

	private static final Log log = LogFactory.getLog(MessageBundle.class);

	private static ResourceBundle[] resources;

	public static final String DEFALUT_GROUP_NAME = "D";
	private static final String MOST_IMPORTANT_FILE_PREFIX = "gecko";
	private static final String I18N_PATH = "/i18n";

	static {

		try {
			File dir = new File(MessageBundle.class.getResource(I18N_PATH)
					.toURI());
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				LinkedList<ResourceBundle> rbs = new LinkedList<ResourceBundle>();
				for (File file : files) {
					try {
						String name = file.getName();
						int index = name.lastIndexOf(".");
						if (index > 0) {
							name = name.substring(0, index);
						}
						ResourceBundle rb = ResourceBundle.getBundle("i18n."
								+ name);
						if (rb != null) {
							if (MOST_IMPORTANT_FILE_PREFIX.equals(name)) {
								rbs.addFirst(rb);
							} else {
								rbs.add(rb);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (rbs.size() > 0) {
					resources = rbs.toArray(new ResourceBundle[rbs.size()]);
				} else {
					resources = new ResourceBundle[0];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			resources = new ResourceBundle[0];
		}
	}

	private MessageBundle() {
	}

	public static String getString(String key) {
		for (ResourceBundle resource : resources) {
			try {
				String value = resource.getString(key);
				if (value != null) {
					return value;
				}
			} catch (Exception e) {
				log.error("### Bundle string: not found!" + key);
				continue;
			}
		}
		return null;
	}

	public static String getString(Class<?> clazz, int index) {
		return getString(clazz, DEFALUT_GROUP_NAME, index);
	}

	public static String getString(Class<?> clazz, int index, Object... args) {
		return getString(clazz, DEFALUT_GROUP_NAME, index, args);
	}

	public static String getString(Class<?> clazz, String group, int index,
			Object... args) {
		String msg = getString(clazz.getName() + "." + group + "." + index);
		if (msg == null)
			return null;
		return MessageFormat.format(msg, args);
	}

	public static void reset() {
		ResourceBundle.clearCache();
	}
}
