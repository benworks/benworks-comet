package benworks.comet.common.utils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author benworks
 * 
 */
public class IdUtils {

	private static final int machineId = Integer.parseInt(System
			.getProperty("machineid"));

	private static final Snowflake GLOBAL_SNOWFLAKE = new Snowflake(machineId);

	private static final Map<String, Snowflake> catalogSnowflakeMap = new ConcurrentHashMap<String, Snowflake>();

	public static String generateStringId() {
		return UUID.randomUUID().toString();
	}

	public static long generateLongId() {
		return GLOBAL_SNOWFLAKE.genId();
	}

	public static long generateLongId(String catalog) {
		if (catalog == null)
			return generateLongId();
		Snowflake snowflake = catalogSnowflakeMap.get(catalog);
		if (snowflake == null) {
			snowflake = new Snowflake(machineId);
			catalogSnowflakeMap.put(catalog, snowflake);
		}
		return snowflake.genId();
	}

}
