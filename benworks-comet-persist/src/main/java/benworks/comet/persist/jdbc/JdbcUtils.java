package benworks.comet.persist.jdbc;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import benworks.comet.persist.Persistable;

/**
 * 
 * @author benworks
 * 
 */
public class JdbcUtils {

	private static Map<Class<?>, TableInfo> tableInfos = new HashMap<Class<?>, TableInfo>();

	private static Map<Class<?>, ParameterizedRowMapper<?>> rowMappers = new HashMap<Class<?>, ParameterizedRowMapper<?>>();

	private static Map<Class<?>, SimpleJdbcInsert> jdbcInserts = new HashMap<Class<?>, SimpleJdbcInsert>();

	public static TableInfo getTableInfo(Class<?> clazz) {
		TableInfo info = tableInfos.get(clazz);
		if (info == null) {
			info = new TableInfo(clazz);
			tableInfos.put(clazz, info);
		}
		return info;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Persistable> RowMapper<T> getRowMapper(
			Class<T> clazz) {
		ParameterizedRowMapper<T> mapper = (ParameterizedRowMapper<T>) rowMappers
				.get(clazz);
		if (mapper == null) {
			mapper = InheritParameterizedBeanPropertyRowMapper
					.createInstance(clazz);
			rowMappers.put(clazz, mapper);
		}
		return mapper;
	}

	public static SimpleJdbcInsert getJdbcInsert(Class<?> clazz,
			DataSource dataSource) {
		SimpleJdbcInsert insert = jdbcInserts.get(clazz);
		if (insert == null) {
			TableInfo tableInfo = getTableInfo(clazz);
			insert = new SimpleJdbcInsert(dataSource);
			insert.withTableName(tableInfo.getTableName());
			jdbcInserts.put(clazz, insert);
		}
		return insert;
	}

}
