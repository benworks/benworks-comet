package benworks.comet.persist.jdbc;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import benworks.comet.common.model.SimplePaginaction;
import benworks.comet.persist.Persistable;

public class JdbcPaginaction<T extends Persistable> extends
		SimplePaginaction<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -747710311998442903L;
	private final static String LIMIT_STRING = " limit ?,?";

	/**
	 * 构建HbnPaginaction对象，完成Hibernate的Criteria数据的分页处理
	 * 
	 * @param jdbcTemplate
	 *            SimpleJdbcTemplate对象
	 * @param pageIndex
	 *            当前页码，超过总页数，表示最后一页
	 * @param pageSize
	 *            每一页显示的条目数，当pageSize<=0时，表示取所有记录
	 * @param clazz
	 *            对象类型
	 * @param sql
	 *            sql语句
	 * @param args
	 *            sql语句的参数
	 */
	public JdbcPaginaction(SimpleJdbcTemplate jdbcTemplate, int pi, int ps,
			Class<T> clazz, String sql, Object... args) {
		boolean pageable = true;
		TableInfo tableInfo = JdbcUtils.getTableInfo(clazz);
		if (ps > 0) {
			int total = jdbcTemplate.queryForInt(
					tableInfo.queryCountByCondition(sql), args);
			pageable = init(pi, ps, total);
		}

		int offset = (pageIndex - 1) * pageSize;
		if (offset < 0) {
			offset = 0;
		}
		//
		String newsql = sql + LIMIT_STRING;
		if (args == null || args.length == 0) {
			elements = jdbcTemplate.query(
					tableInfo.queryByCondition(newsql, false),
					JdbcUtils.getRowMapper(clazz), offset, pageSize);
		} else {
			int length = args.length;
			Object[] newargs = new Object[length + 2];
			System.arraycopy(args, 0, newargs, 0, length);
			newargs[length] = offset;
			newargs[length + 1] = pageSize;
			elements = jdbcTemplate.query(
					tableInfo.queryByCondition(newsql, false),
					JdbcUtils.getRowMapper(clazz), newargs);
		}
		if (tableInfo.isCache() && !tableInfo.isHasListIgnore()
				&& !tableInfo.isHasSubTable()) {
			for (T t : elements)
				CacheManager.getCache(clazz).set(t);
		}

		if (!pageable) {
			total = pageSize = elements.size();
		}
	}

}
