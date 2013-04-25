/**
 * 
 */
package benworks.comet.common.model;

import net.sf.json.JSONObject;

/**
 * 可改写的JsonProperties
 * 
 * @author benworks
 * 
 */
public class WriteableJsonProperties extends JsonProperties {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3939236056822568740L;

	public WriteableJsonProperties(JSONObject json) {
		super(json);
	}

	public WriteableJsonProperties(String jsonString) {
		super(jsonString);
	}

	/**
	 * 增加或者更新属性
	 * 
	 * @param key
	 * @param value
	 */
	public synchronized void setProperty(String key, Object value) {
		if (key == null || value == null) {
			return;
		}
		if (json == null || json.isEmpty()) {
			json = new JSONObject();
		}
		json.put(key, value);
	}

	/**
	 * 删除属性
	 * 
	 * @param key
	 * @param value
	 */
	public synchronized void removeProperty(String key) {
		if (key == null) {
			return;
		}
		if (json == null || json.isEmpty()) {
			json = new JSONObject();
		}
		json.remove(key);
	}

}
