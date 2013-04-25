package benworks.comet.common.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;

/**
 * 
 * @author benworks
 * 
 */
public class XlsUtils {

	public static final String DATA_FILE_SUFFIX = ".xls";
	public static final String ID_FIELD_NAME = "id";
	private static final Log log = LogFactory.getLog(XlsUtils.class);

	public static int fieldDefineRow = 1;
	public static int dataStartRow = 3;

	/**
	 * 遍历目录加载所有的数据，数据类通过文件名自动匹配<br>
	 * 文件名和类名的匹配规则：FullClassName_suffix.xls
	 * 
	 * @param <T>
	 * 
	 * @param dir
	 *            数据文件所在的目录，不包含子目录，只识别.xls文件
	 * @return
	 */
	public static <T> Map<Class<T>, Map<Integer, T>> loadOneDir(String dir) {
		Map<Class<T>, Map<Integer, T>> all = new ConcurrentHashMap<Class<T>, Map<Integer, T>>();
		File dirFile = new File(dir);
		if (!dirFile.exists())
			return all;
		File[] dataFiles = dirFile.listFiles();
		for (File file : dataFiles) {
			try {
				if (file.isDirectory())
					continue;
				@SuppressWarnings("unchecked")
				Class<T> clazz = (Class<T>) getFullClassNameByFile(file
						.getName());
				if (clazz == null)
					continue;
				Map<Integer, T> map = load(clazz, file);
				Map<Integer, T> clazzMap = all.get(clazz);
				if (clazzMap == null)
					clazzMap = new ConcurrentHashMap<Integer, T>();
				clazzMap.putAll(map);
				all.put(clazz, clazzMap);
			} catch (Exception e) {
				e.printStackTrace();
				log.warn("### " + file.getAbsolutePath() + " data init fail");
			}
		}
		return all;
	}

	/**
	 * 从数据文件加载数据，存放到一个map当中，每个数据文件必须要有一个id列
	 * 
	 * @param <T>
	 * @param clazz
	 * @param file
	 * @return
	 */
	public static <T> Map<Integer, T> load(Class<T> clazz, File file) {
		Map<Integer, T> rows = Collections.emptyMap();
		if (!file.exists())
			return rows;
		HSSFWorkbook wb = null;
		try {
			InputStream fis = new FileInputStream(file);
			POIFSFileSystem poifs = new POIFSFileSystem(fis);
			wb = new HSSFWorkbook(poifs);
		} catch (Exception e) {
			e.printStackTrace();
			return rows;
		}

		HSSFSheet sheet = wb.getSheetAt(0);
		int rowcount = sheet.getLastRowNum();
		if (rowcount < dataStartRow)
			return rows;
		HSSFRow fieldRow = sheet.getRow(fieldDefineRow);
		Iterator<Cell> iterator = fieldRow.cellIterator();
		List<Cell> fieldList = new ArrayList<Cell>();
		while (iterator.hasNext()) {
			fieldList.add(iterator.next());
		}
		checkDiff(clazz, file, fieldList);
		rows = new ConcurrentHashMap<Integer, T>(rowcount);
		for (int i = dataStartRow; i <= rowcount; ++i) {
			try {
				HSSFRow row = sheet.getRow(i);
				if (row == null)
					continue;
				Map<String, Object> propertyMap = new HashMap<String, Object>();
				for (int j = 0; j < fieldList.size(); j++) {
					HSSFCell cell = row.getCell(j);
					if (cell == null)
						continue;
					Cell fieldCell = fieldList.get(j);
					String key = fieldCell.getStringCellValue();
					Object value = null;
					if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
						value = cell.getStringCellValue();
					} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						value = cell.getNumericCellValue();
					}
					propertyMap.put(key, value);
				}
				T t = clazz.newInstance();
				BeanUtils.populate(t, propertyMap);
				Double id = (Double) propertyMap.get(ID_FIELD_NAME);
				if (id == null || id.intValue() == 0) {
					log.warn("### " + file.getAbsolutePath()
							+ " miss id field or id=0");
					continue;
				}
				rows.put(id.intValue(), t);

			} catch (Exception e) {
				e.printStackTrace();
				log.warn("### " + file.getAbsolutePath() + " data init fail");
			}
		}
		log.info("### " + clazz.getName() + " data init from "
				+ file.getAbsolutePath() + ",size: " + rows.size());
		return rows;
	}

	public static boolean isSuitableFile(File file) {
		return (file.getName().toLowerCase()
				.lastIndexOf(XlsUtils.DATA_FILE_SUFFIX) > 0);
	}

	public static Class<?> getFullClassNameByFile(String fileName) {
		Class<?> clazz = null;
		try {
			int index = fileName.toLowerCase().lastIndexOf(DATA_FILE_SUFFIX);
			if (index < 0)
				return null;
			String className = fileName.substring(0, index);
			index = className.lastIndexOf("_");
			if (index > 0)
				className = className.substring(0, index);
			clazz = (Class<?>) Class.forName(className);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return clazz;
	}

	private static void checkDiff(Class<?> clazz, File file,
			List<Cell> fieldList) {
		try {
			List<String> fields = new ArrayList<String>(fieldList.size());
			for (Cell cell : fieldList) {
				fields.add(cell.getStringCellValue());
			}
			//
			Object sample = clazz.newInstance();
			Map<?, ?> map = BeanUtils.describe(sample);
			for (Object key : map.keySet()) {
				if ("class".equals(key.toString()))
					continue;
				if (!fields.contains(key)) {
					log.warn("### " + file.getAbsolutePath() + " miss field "
							+ key + " in " + clazz.getName());
				}
			}
			for (Cell cell : fieldList) {
				String fieldName = cell.getStringCellValue();
				if (!map.containsKey(fieldName)) {
					log.warn("### " + clazz.getName() + " miss field "
							+ fieldName + " in " + file.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
