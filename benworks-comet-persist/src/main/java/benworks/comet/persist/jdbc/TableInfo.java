package benworks.comet.persist.jdbc;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import benworks.comet.persist.Persistable;
import benworks.comet.persist.annotation.ListIgnore;
import benworks.comet.persist.annotation.PersistEntity;

class TableInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2410433805902821904L;
	private Class<?> clazz;
	private String tableName;
	private String idColumnName = "id";
	private String discriminatorColumnName;
	private Integer discriminatorValue;
	private Map<Integer, Class<?>> discriminatorMap;
	private String[] updateProperties;
	private String[] listProperties;
	private boolean hasListIgnore;
	private TableInfo parent;
	private boolean immutable;
	private boolean hasSubTable;
	private boolean cache = true;

	public TableInfo(Class<?> clazz) {
		Entity entityAnnotation = clazz.getAnnotation(Entity.class);
		if (entityAnnotation == null) {
			throw new RuntimeException("not persist entity type:"
					+ clazz.getName());
		}
		parseClass(clazz);
		parseMethod(clazz);
		this.clazz = clazz;
	}

	private void parseClass(Class<?> clazz) {
		// sub class
		DiscriminatorValue discriminatorValueAnnotation = clazz
				.getAnnotation(DiscriminatorValue.class);
		if (discriminatorValueAnnotation != null) {
			Class<?> superClass = clazz.getSuperclass();
			while (superClass != null) {
				try {
					DiscriminatorColumn discriminatorAnnotation = superClass
							.getAnnotation(DiscriminatorColumn.class);
					if (discriminatorAnnotation != null) {
						TableInfo tableInfo = JdbcUtils
								.getTableInfo(superClass);
						idColumnName = tableInfo.getIdColumnName();
						parent = tableInfo;
						discriminatorValue = Integer
								.valueOf(discriminatorValueAnnotation.value());
						if (tableInfo.discriminatorMap == null) {
							tableInfo.discriminatorMap = new HashMap<Integer, Class<?>>();
						}
						tableInfo.discriminatorMap.put(discriminatorValue,
								clazz);
						tableInfo.hasSubTable = clazz
								.getAnnotation(Table.class) != null;
						immutable = tableInfo.immutable;
						cache = tableInfo.cache;
						break;
					}
				} catch (Exception e) {

				}
				superClass = superClass.getSuperclass();
			}
		}
		// root class
		Table tableAnnotation = clazz.getAnnotation(Table.class);
		if (tableAnnotation != null && tableAnnotation.name() != null) {
			tableName = tableAnnotation.name();
		}
		PersistEntity entityAnnotation = clazz
				.getAnnotation(PersistEntity.class);
		if (entityAnnotation != null) {
			immutable = entityAnnotation.immutable();
			cache = entityAnnotation.cache();
		}
		DiscriminatorColumn discriminatorAnnotation = clazz
				.getAnnotation(DiscriminatorColumn.class);
		if (discriminatorAnnotation != null
				&& discriminatorAnnotation.name() != null) {
			discriminatorColumnName = discriminatorAnnotation.name();
		}
	}

	private void parseMethod(Class<?> clazz) {
		PropertyDescriptor[] descriptors = null;
		BeanInfo beanInfo = null;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			e.printStackTrace();
			return;
		}
		descriptors = beanInfo.getPropertyDescriptors();
		if (descriptors == null) {
			descriptors = new PropertyDescriptor[0];
		}
		List<String> uproperties = new ArrayList<String>();
		Set<String> lproperties = new HashSet<String>();
		for (int i = 0; i < descriptors.length; i++) {
			try {
				String name = descriptors[i].getName();
				if ("class".equals(name)) {
					continue;
				}
				Method readMethod = descriptors[i].getReadMethod();
				if (readMethod == null)
					continue;
				Transient transientAnnotation = readMethod
						.getAnnotation(Transient.class);
				if (transientAnnotation != null) {
					continue;
				}
				if (name.equals(idColumnName)) {
					lproperties.add(name);
					continue;
				}
				if (name.equals(discriminatorColumnName)) {
					lproperties.add(name);
					continue;
				}
				Column columnAnnotation = readMethod
						.getAnnotation(Column.class);
				if (columnAnnotation != null && !columnAnnotation.updatable()) {
					lproperties.add(name);
					continue;
				}
				Id idAnnotation = readMethod.getAnnotation(Id.class);
				if (idAnnotation != null) {
					idColumnName = name;
				}
				uproperties.add(name);
				ListIgnore listIgnoreAnnotation = readMethod
						.getAnnotation(ListIgnore.class);
				if (listIgnoreAnnotation != null) {
					hasListIgnore = true;
					continue;
				}
				lproperties.add(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		updateProperties = uproperties.toArray(new String[uproperties.size()]);
		if (hasListIgnore)
			listProperties = lproperties
					.toArray(new String[lproperties.size()]);
		else
			listProperties = new String[] { "*" };
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public boolean isImmutable() {
		return immutable;
	}

	public boolean isCache() {
		return cache;
	}

	public String[] getUpdateProperties() {
		return updateProperties;
	}

	public boolean isHasListIgnore() {
		return hasListIgnore;
	}

	public boolean isHasSubTable() {
		return hasSubTable;
	}

	public String getTableName() {
		TableInfo p = this;
		String tn = p.tableName;
		while (tn == null) {
			p = p.getParent();
			if (p == null)
				break;
			tn = p.tableName;
		}
		if (tn != null)
			return tn;
		return null;
	}

	public String getIdColumnName() {
		return idColumnName;
	}

	public String getDiscriminatorColumnName() {
		return discriminatorColumnName;
	}

	public Map<Integer, Class<?>> getDiscriminatorMap() {
		return discriminatorMap;
	}

	public TableInfo getParent() {
		return parent;
	}

	public Integer getDiscriminatorValue() {
		return discriminatorValue;
	}

	private String tableString() {
		if (tableName == null && parent != null) {
			return parent.tableString();
		}
		if (parent == null)
			return tableName;
		else if (parent.getTableName().equals(tableName))
			return tableName;
		else {
			String parentTableName = parent.getTableName();
			if (parentTableName == null)
				return tableName;
			StringBuilder builder = new StringBuilder();
			builder.append(tableName);
			builder.append(" left join ");
			builder.append(parentTableName);
			builder.append(" on ");
			builder.append(tableName + "." + idColumnName);
			builder.append("=");
			builder.append(parentTableName + "." + parent.getIdColumnName());
			return builder.toString();
		}
	}

	public String queryByPk(Serializable id, boolean update) {
		StringBuilder builder = new StringBuilder();
		builder.append("select * from ");
		builder.append(tableString());
		builder.append(" where ");
		builder.append(getTableName() + "." + idColumnName);
		builder.append("=?");
		builder.append(update ? " for update" : "");
		return builder.toString();
	}

	public String queryByCondition(String condition, boolean single) {
		StringBuilder cols = new StringBuilder();
		if (single) {
			cols.append("*");
		} else {
			for (int i = 0; i < listProperties.length; i++) {
				cols.append(listProperties[i]);
				if (i < listProperties.length - 1)
					cols.append(",");
			}
		}
		StringBuilder builder = new StringBuilder();
		builder.append("select " + cols + " from ");
		builder.append(tableString());
		builder.append(" ");
		builder.append(condition);
		if (discriminatorValue != null) {
			if (StringUtils.isEmpty(condition))
				builder.append(" where ");
			else
				builder.append(" and ");
			builder.append(parent.discriminatorColumnName + "="
					+ discriminatorValue);
		}
		return builder.toString();
	}

	public String queryCountByCondition(String condition) {
		StringBuilder builder = new StringBuilder();
		builder.append("select count(*) from ");
		builder.append(tableString());
		builder.append(" ");
		builder.append(condition);
		if (discriminatorValue != null) {
			if (StringUtils.isEmpty(condition))
				builder.append(" where ");
			else
				builder.append(" and ");
			builder.append(parent.discriminatorColumnName + "="
					+ discriminatorValue);
		}
		return builder.toString();
	}

	public String deleteByPk(Serializable id) {
		StringBuilder builder = new StringBuilder();
		builder.append("delete from ");
		builder.append(getTableName());
		builder.append(" where ");
		builder.append(idColumnName);
		builder.append("=?");
		return builder.toString();
	}

	public String deleteByCondition(String condition) {
		StringBuilder builder = new StringBuilder();
		builder.append("delete from ");
		builder.append(getTableName());
		builder.append(" ");
		builder.append(condition);
		return builder.toString();
	}

	public String updateByPk(Persistable t) {
		String[] needUpdateProperties = DynamicUpdate.getUpdateProperties(t);
		if (needUpdateProperties == null) {
			needUpdateProperties = updateProperties;
		}
		if (needUpdateProperties.length == 0) {
			return "";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("update ");
		builder.append(getTableName());
		builder.append(" set ");
		for (int i = 0; i < needUpdateProperties.length; ++i) {
			String property = needUpdateProperties[i];
			builder.append(property + "=:" + property);
			if (i < needUpdateProperties.length - 1)
				builder.append(",");
		}
		builder.append(" where ");
		builder.append(idColumnName);
		builder.append("=:");
		builder.append(idColumnName);
		return builder.toString();
	}

	public String updateByCondition(String condition) {
		StringBuilder builder = new StringBuilder();
		builder.append("update ");
		builder.append(getTableName());
		builder.append(" ");
		builder.append(condition);
		return builder.toString();
	}

}