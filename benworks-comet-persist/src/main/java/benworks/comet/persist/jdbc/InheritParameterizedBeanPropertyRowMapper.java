package benworks.comet.persist.jdbc;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

import benworks.comet.persist.Persistable;

class InheritParameterizedBeanPropertyRowMapper<T extends Persistable>
		implements ParameterizedRowMapper<T> {

	private Class<T> inheritClass;

	private static Map<Class<?>, Map<String, Property>> inheritRowMappers = new HashMap<Class<?>, Map<String, Property>>();

	@SuppressWarnings("unchecked")
	public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
		Assert.state(this.inheritClass != null,
				"Mapped inherit class was not specified");
		TableInfo tableInfo = benworks.comet.persist.jdbc.JdbcUtils
				.getTableInfo(inheritClass);
		Class<? extends T> mappedClass = inheritClass;
		if (tableInfo.getDiscriminatorMap() != null
				&& tableInfo.getDiscriminatorColumnName() != null) {
			Integer division = rs
					.getInt(tableInfo.getDiscriminatorColumnName());
			if (division != null) {
				Class<?> divisionClass = tableInfo.getDiscriminatorMap().get(
						division);
				if (divisionClass != null) {
					mappedClass = (Class<? extends T>) divisionClass;
				}
			}
		}
		//
		return (T) mapRow(mappedClass, rs, rowNumber);

	}

	private <TT extends T> TT mapRow(Class<TT> mappedClass, ResultSet rs,
			int rowNumber) throws SQLException {
		TT mappedObject = (TT) BeanUtils.instantiateClass(mappedClass);
		mapRow(mappedObject, rs, rowNumber);
		return mappedObject;
	}

	private <TT extends T> TT mapRow(TT mappedObject, ResultSet rs,
			int rowNumber) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		for (int index = 1; index <= columnCount; index++) {
			String column = JdbcUtils.lookupColumnName(rsmd, index)
					.toLowerCase();
			Property property = (Property) getMappedFields(
					mappedObject.getClass()).get(column);
			if (property != null) {
				try {
					Object value = getColumnValue(rs, index, property.getType());
					if (value == null)
						continue;
					Method writeMethod = property.getWriteMethod();
					if (!writeMethod.isAccessible())
						writeMethod.setAccessible(true);
					writeMethod.invoke(mappedObject, value);
				} catch (Exception ex) {
					throw new DataRetrievalFailureException(
							"Unable to map column " + column + " to property "
									+ property.getName(), ex);
				}
			}
		}
		return mappedObject;
	}

	private Map<String, Property> getMappedFields(Class<?> mappedClass) {
		Map<String, Property> mappedFields = inheritRowMappers.get(mappedClass);
		if (mappedFields == null) {
			mappedFields = new HashMap<String, Property>();
			PropertyDescriptor[] pds = BeanUtils
					.getPropertyDescriptors(mappedClass);
			for (int i = 0; i < pds.length; i++) {
				PropertyDescriptor pd = pds[i];
				Method writeMethod = BeanUtils.findMethod(mappedClass, "set"
						+ StringUtils.capitalize(pd.getName()),
						new Class[] { pd.getPropertyType() });
				if (writeMethod != null) {
					if (!writeMethod.isAccessible())
						writeMethod.setAccessible(true);

				}
				if (writeMethod != null)
					mappedFields.put(pd.getName().toLowerCase(), new Property(
							pd.getName(), pd.getPropertyType(), writeMethod));
			}
			inheritRowMappers.put(mappedClass, mappedFields);
		}
		return mappedFields;
	}

	protected Object getColumnValue(ResultSet rs, int index, Class<?> type)
			throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index, type);
	}

	private InheritParameterizedBeanPropertyRowMapper() {

	}

	public static <T extends Persistable> InheritParameterizedBeanPropertyRowMapper<T> createInstance(
			Class<T> inheritClass) {
		InheritParameterizedBeanPropertyRowMapper<T> newInstance = new InheritParameterizedBeanPropertyRowMapper<T>();
		newInstance.inheritClass = inheritClass;
		return newInstance;
	}

	static class Property {
		private String name;
		private Class<?> type;
		private Method writeMethod;

		public Property(String name, Class<?> type, Method writeMethod) {
			this.name = name;
			this.type = type;
			this.writeMethod = writeMethod;
		}

		public String getName() {
			return name;
		}

		public Class<?> getType() {
			return type;
		}

		public Method getWriteMethod() {
			return writeMethod;
		}

	}
}
