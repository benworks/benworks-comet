package benworks.comet.codegen.as;

import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.qdox.model.Type;

public class As3TypeMap {
	private static final Map<String, String> actionScriptTypeMap = new HashMap<String, String>();

	static {
		actionScriptTypeMap.put("boolean", "Boolean");
		actionScriptTypeMap.put("int", "int");
		actionScriptTypeMap.put("long", "Number");
		actionScriptTypeMap.put("short", "Number");
		actionScriptTypeMap.put("float", "Number");
		actionScriptTypeMap.put("double", "Number");
		actionScriptTypeMap.put("byte", "Number");
		actionScriptTypeMap.put("java.lang.String", "String");
		actionScriptTypeMap.put("java.lang.Boolean", "Boolean");
		actionScriptTypeMap.put("java.lang.Integer", "int");
		actionScriptTypeMap.put("java.lang.Long", "Number");
		actionScriptTypeMap.put("java.lang.Short", "Number");
		actionScriptTypeMap.put("java.lang.Float", "Number");
		actionScriptTypeMap.put("java.lang.Double", "Number");
		actionScriptTypeMap.put("java.lang.Byte", "Number");
		actionScriptTypeMap.put("java.lang.Number", "Number");
		actionScriptTypeMap.put("java.sql.Timestamp", "Date");
		actionScriptTypeMap.put("java.sql.Date", "Date");
		actionScriptTypeMap.put("java.util.Date", "Date");
		actionScriptTypeMap.put("java.util.Collection", "Array");
		actionScriptTypeMap.put("java.util.List", "Array");
		actionScriptTypeMap.put("java.util.ArrayList", "Array");
		actionScriptTypeMap.put("java.util.Set", "Array");
		actionScriptTypeMap.put("java.util.HashSet", "Array");
		actionScriptTypeMap.put("java.util.Map", "Object");
		actionScriptTypeMap.put("java.util.HashMap", "Object");
	}

	/**
	 * java type to actionscript type
	 * 
	 * @param type
	 * @return
	 */
	public static String toActionScriptType(Type type) {
		if (type.getJavaClass().isA("java.util.Date")) {
			return "Date";
		}
		if (type.getJavaClass().isA("java.util.Collection")) {
			return "Array";
		}
		if (type.isArray() && "byte".equalsIgnoreCase(type.getValue())) {
			return "ByteArray";
		}
		if (type.isArray()) {
			return "Array";
		}
		if (type.getJavaClass().isA("java.util.Map")) {
			return "Object";
		}
		String result = type.getValue();
		if (actionScriptTypeMap.get(result) != null) {
			return actionScriptTypeMap.get(result);
		}

		return result;
	}

	/**
	 * java type to actionscript type
	 * 
	 * @param type
	 * @return
	 */
	public static String toActionScriptType(String type) {
		if (actionScriptTypeMap.get(type) != null) {
			return actionScriptTypeMap.get(type);
		}
		return type;
	}
}
