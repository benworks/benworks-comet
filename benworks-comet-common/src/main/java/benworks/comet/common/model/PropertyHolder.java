package benworks.comet.common.model;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 变量保存
 * 
 * @author benworks
 * 
 */
public class PropertyHolder {
	private static final Log log = LogFactory.getLog(PropertyHolder.class);

	public void setValues(String jsonString) {
		JsonProperties json = new JsonProperties(jsonString);
		for (Entry<String, Object> entry : json.getEntrySet()) {

			String property = entry.getKey();
			Integer value = (Integer) entry.getValue();
			try {
				Field field = getClass().getField(property);
				field.set(this, value);
				// PropertyDescriptor pd =
				// BeanUtils.getPropertyDescriptor(getClass(), property);
				// int propertyValue = (Integer)
				// (pd.getReadMethod().invoke(this));
				// pd.getWriteMethod().invoke(this, propertyValue + value);
			} catch (Exception e) {
				log.error("### setValue " + property);
				e.printStackTrace();
			}

		}
	}

	public int getValue(String property) {
		int propertyValue = -1;
		try {
			Field field = getClass().getField(property);
			propertyValue = field.getInt(this);
			// PropertyDescriptor pd =
			// BeanUtils.getPropertyDescriptor(getClass(), property);
			// propertyValue = (Integer) (pd.getReadMethod().invoke(this));
		} catch (Exception e) {
			log.error("### getValue " + property);
			e.printStackTrace();
		}

		return propertyValue;
	}

	public void upgrade(String oldJsonString, String newJsonString) {
		JsonProperties oldJson = new JsonProperties(oldJsonString);
		for (Entry<String, Object> entry : oldJson.getEntrySet()) {
			String property = entry.getKey();
			Integer value = (Integer) entry.getValue();
			try {
				Field field = getClass().getField(property);
				int propertyValue = field.getInt(this);
				propertyValue -= value;
				field.set(this, value);
				// PropertyDescriptor pd =
				// BeanUtils.getPropertyDescriptor(getClass(), property);
				// int propertyValue = (Integer)
				// (pd.getReadMethod().invoke(this));
				// propertyValue -= value;
				// pd.getWriteMethod().invoke(this, propertyValue);
			} catch (Exception e) {
				log.error("### update " + property);
				e.printStackTrace();
			}
		}
		//
		JsonProperties newJson = new JsonProperties(newJsonString);
		for (Entry<String, Object> entry : newJson.getEntrySet()) {
			String property = entry.getKey();
			Integer value = (Integer) entry.getValue();
			try {
				Field field = getClass().getField(property);
				int propertyValue = field.getInt(this);
				propertyValue += value;
				field.set(this, value);
				// PropertyDescriptor pd =
				// BeanUtils.getPropertyDescriptor(getClass(), property);
				// int propertyValue = (Integer)
				// (pd.getReadMethod().invoke(this));
				// propertyValue += value;
				// pd.getWriteMethod().invoke(this, propertyValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
