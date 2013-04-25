package benworks.comet.common.data;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import benworks.comet.common.timer.ScheduleManager;

/**
 * 游戏数据监控
 * 
 * @author benworks
 * 
 */
@Service
public class StaticDataManager {

	public static String replaceDirString = "replace";
	public static String deleteDirString = "delete";
	private static final Log log = LogFactory.getLog(StaticDataManager.class);

	@Value("${game.data.dir}")
	private String dataDir;
	@Value("${game.data.check.period}")
	private int checkPeriod = 60;// in sceond

	@Autowired
	private ScheduleManager scheduleManager;

	@Autowired
	private Set<DataChangeListener> dataChangeListeners;

	private Map<Class<?>, List<DataChangeListener>> listenerMap;

	private Map<Class<?>, Map<Integer, Object>> allData = new ConcurrentHashMap<Class<?>, Map<Integer, Object>>();

	// key:filePath,value:lastUpdateTimeInMs
	private Map<String, Long> fileTimeMap = new HashMap<String, Long>();

	@PostConstruct
	public void init() {
		initListeners();
		check();
		scheduleManager.scheduleAtFixedRate(new UpdateDataTask(), checkPeriod
				* DateUtils.MILLIS_PER_SECOND, checkPeriod
				* DateUtils.MILLIS_PER_SECOND);
	}

	private void initListeners() {
		if (dataChangeListeners == null)
			return;
		listenerMap = new HashMap<Class<?>, List<DataChangeListener>>(
				dataChangeListeners.size());
		for (DataChangeListener listener : dataChangeListeners) {
			Class<?>[] clazzAry = listener.interestClass();
			for (Class<?> clazz : clazzAry) {
				List<DataChangeListener> listeners = listenerMap.get(clazz);
				if (listeners == null) {
					listeners = new ArrayList<DataChangeListener>();
					listenerMap.put(clazz, listeners);
				}
				listeners.add(listener);
			}
		}
	}

	private void fireChangeEvent(Class<?> clazz, Map<Integer, Object> data) {
		if (listenerMap == null)
			return;
		List<DataChangeListener> listeners = listenerMap.get(clazz);
		if (listeners == null || listeners.size() == 0)
			return;
		for (DataChangeListener listener : listeners) {
			try {
				listener.change(clazz, data);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Map<Integer, T> getMap(Class<T> clazz) {
		return (Map<Integer, T>) allData.get(clazz);
	}

	@SuppressWarnings("unchecked")
	public <T> T getMap(Class<T> clazz, int id) {
		Map<Integer, T> map = (Map<Integer, T>) allData.get(clazz);
		if (map == null)
			return null;
		return map.get(id);
	}

	private void check() {
		log.info("### dataDirs:" + dataDir);
		String[] dirs = dataDir.split(",");
		for (String dirString : dirs) {
			try {
				URL url = getClass().getResource(dirString);
				if (url == null)
					continue;
				File dir = new File(url.toURI());
				log.info("### dataDir:" + dir);
				if (!dir.exists())
					continue;
				// add or update
				checkUpdate(dir);
				log.info("### Data add or update");
				// replace
				File replaceDir = new File(dir, replaceDirString);
				if (replaceDir.exists() && replaceDir.isDirectory()) {
					checkUpdate(dir);
					log.info("### Data replace");
				}
				// delete
				File deleteDir = new File(dir, deleteDirString);
				if (deleteDir.exists() && deleteDir.isDirectory()) {
					checkDelete(dir);
					log.info("### Data delete");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void checkUpdate(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (!XlsUtils.isSuitableFile(file))
				continue;
			Long lastUpdateTime = fileTimeMap.get(file.getAbsolutePath());
			if (lastUpdateTime != null && file.lastModified() <= lastUpdateTime)
				continue;
			Class<?> clazz = XlsUtils.getFullClassNameByFile(file.getName());
			Map<Integer, ?> map = XlsUtils.load(clazz, file);
			Map<Integer, Object> classMap = allData.get(clazz);
			if (classMap == null) {
				classMap = new ConcurrentHashMap<Integer, Object>();
				allData.put(clazz, classMap);
			}
			classMap.putAll(map);
			fireChangeEvent(clazz, classMap);
			fileTimeMap.put(file.getAbsolutePath(), file.lastModified());
		}
	}

	private void checkDelete(File dir) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (!XlsUtils.isSuitableFile(file))
				continue;
			Long lastUpdateTime = fileTimeMap.get(file.getAbsolutePath());
			if (lastUpdateTime != null && file.lastModified() <= lastUpdateTime)
				continue;
			Class<?> clazz = XlsUtils.getFullClassNameByFile(file.getName());
			Map<Integer, Object> classMap = allData.get(clazz);
			if (classMap == null)
				continue;
			Map<Integer, ?> map = XlsUtils.load(clazz, file);
			for (Integer id : map.keySet()) {
				classMap.remove(id);
			}
			fireChangeEvent(clazz, classMap);
			fileTimeMap.put(file.getAbsolutePath(), file.lastModified());
		}
	}

	private class UpdateDataTask implements Runnable {

		@Override
		public void run() {
			check();
		}

	}
}
