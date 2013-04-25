/**
 * 
 */
package benworks.comet.common.model;

import java.io.Serializable;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 通常情况下是把一个json字符串转化而成，然后通过此类方便访问
 * 
 * @author benworks
 * 
 */
public class JsonProperties implements Serializable {

	private static final long serialVersionUID = 7532927868052107089L;
	transient protected JSONObject json;
	public final static JSONObject EMPTY_JSON = new JSONObject();
	public final static JsonProperties EMPTY_JSON_PROPERTIES = new JsonProperties(
			"");

	public JsonProperties(String jsonString) {
		if (StringUtils.isNotEmpty(jsonString)) {
			try {
				json = JSONObject.fromObject(jsonString);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (json == null)
			json = EMPTY_JSON;
	}

	public JsonProperties(JSONObject json) {
		if (json == null) {
			json = EMPTY_JSON;
		}
		this.json = json;
	}

	public JsonProperties() {
		this.json = EMPTY_JSON;
	}

	public boolean has(String key) {
		return json.has(key);
	}

	public int getInt(String key) {
		return json.optInt(key);
	}

	public int getInt(String key, int defaultValue) {
		return json.optInt(key, defaultValue);
	}

	public long getLong(String key) {
		return json.optLong(key);
	}

	public long getLong(String key, long defaultValue) {
		return json.optLong(key, defaultValue);
	}

	public String getString(String key) {
		return json.optString(key);
	}

	public String getString(String key, String defaultValue) {
		return json.optString(key, defaultValue);
	}

	public Object getObject(String key) {
		return json.get(key);
	}

	public boolean getBoolean(String key) {
		return json.optBoolean(key);
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return json.optBoolean(key, false);
	}

	public int[] getInts(String indexString, String split) {
		if (StringUtils.isEmpty(indexString))
			return ArrayUtils.EMPTY_INT_ARRAY;
		String[] indexAry = indexString.split(split);
		int[] indexs = new int[indexAry.length];
		try {
			for (int i = 0; i < indexAry.length; i++) {
				if (StringUtils.isEmpty(indexAry[i]))
					continue;
				indexs[i] = Integer.parseInt(indexAry[i]);
			}
			return indexs;
		} catch (Exception e) {
			e.printStackTrace();
			return ArrayUtils.EMPTY_INT_ARRAY;
		}
	}

	public String toString() {
		return json.toString();
	}

	@SuppressWarnings("unchecked")
	public Set<Entry<String, Object>> getEntrySet() {
		return json.entrySet();
	}

	public int size() {
		return json.size();
	}
}
