/**
 * 
 */
package benworks.comet.common.model;

import java.util.Collections;
import java.util.List;

/**
 * 分页接口的简单实现
 * 
 * @author benworks
 */
public class SimplePaginaction<T> implements Pageable<T> {

	private static final long serialVersionUID = 8141620322440739233L;

	public final static int MAX_RESULT_SIZE = 1000;

	protected List<T> elements;

	protected int pageSize;

	protected int pageIndex;

	protected int total = 0;

	protected int pageCount;

	protected int offset = 0;

	@SuppressWarnings("unchecked")
	public final static Pageable EMPTY_PAGINACTION = new SimplePaginaction();

	@SuppressWarnings("unchecked")
	public static <T> Pageable<T> getEmptyPageable() {
		return EMPTY_PAGINACTION;
	}

	protected SimplePaginaction() {
		elements = Collections.emptyList();
		pageSize = 0;
		pageIndex = 1;
		total = 0;
		pageCount = 1;
		offset = 0;
	}

	protected boolean init(int pi, int ps, int to) {
		this.pageIndex = pi;
		this.pageSize = ps;
		this.total = to;
		if (this.pageSize > MAX_RESULT_SIZE) {
			this.pageSize = MAX_RESULT_SIZE;
		}
		boolean pageable = true;

		if (pageSize > 0) {
			pageCount = (this.total % this.pageSize == 0) ? (this.total / this.pageSize)
					: (this.total / this.pageSize) + 1;
		} else {
			pageSize = MAX_RESULT_SIZE;
			pageCount = 1;
			pageable = false;
		}
		if (pageCount < 1) {
			pageCount = 1;
		}
		if (this.pageIndex > pageCount) {
			this.pageIndex = pageCount;
		}
		if (this.pageIndex < 1) {
			this.pageIndex = 1;
		}
		this.offset = (this.pageIndex - 1) * this.pageSize;
		if (this.offset < 0) {
			this.offset = 0;
		}
		return pageable;
	}

	/**
	 * 构建SimplePaginaction对象
	 * 
	 * @param List
	 *            elements列表
	 * @param pi
	 *            当前页码，超过总页数，表示最后一页
	 * @param ps
	 *            每一页显示的条目数，当pageSize<=0时，表示取所有记录
	 * @param to
	 *            总数
	 * @throws DBException
	 */
	public SimplePaginaction(final List<T> es, final int pi, final int ps,
			final int to) {

		boolean pageable = init(pi, ps, to);
		elements = es;
		if (!pageable) {
			total = pageSize = elements.size();
		}
	}

	public int getPageCount() {
		return pageCount;
	}

	public List<T> getElements() {
		return elements;
	}

	public int getTotal() {
		return this.total;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public int getPageIndex() {
		return this.pageIndex;
	}

	public int getOffset() {
		return this.offset;
	}

}
