package benworks.comet.persist.jdbc;

import java.sql.Types;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

class InheritBeanPropertySqlParameterSource extends
		BeanPropertySqlParameterSource {

	private TableInfo thisTableInfo;

	public InheritBeanPropertySqlParameterSource(Object object) {
		super(object);
		thisTableInfo = JdbcUtils.getTableInfo(object.getClass());
	}

	@Override
	public int getSqlType(String paramName) {
		if (thisTableInfo.getParent() != null
				&& paramName.equals(thisTableInfo.getParent()
						.getDiscriminatorColumnName())) {
			return Types.INTEGER;
		}
		return super.getSqlType(paramName);
	}

	@Override
	public Object getValue(String paramName) throws IllegalArgumentException {
		if (thisTableInfo.getParent() != null
				&& paramName.equals(thisTableInfo.getParent()
						.getDiscriminatorColumnName())) {
			return thisTableInfo.getDiscriminatorValue();
		}
		return super.getValue(paramName);
	}

	@Override
	public boolean hasValue(String paramName) {
		if (thisTableInfo.getParent() != null
				&& paramName.equals(thisTableInfo.getParent()
						.getDiscriminatorColumnName())) {
			return true;
		}
		return super.hasValue(paramName);
	}

}
