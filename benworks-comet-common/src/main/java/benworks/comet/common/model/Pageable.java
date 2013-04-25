package benworks.comet.common.model;

import java.io.Serializable;
import java.util.List;

/**
 * 分页数据的抽象接口
 * 
 * @author benworks
 */
public interface Pageable<T> extends Serializable {
	/**
	 * 获取该查询结果集的总页数
	 * 
	 * @return
	 */
	int getPageCount();

	/**
	 * 获得当前页的数据
	 * 
	 * @return
	 */
	List<T> getElements();

	/**
	 * 查询结果的数据总数
	 * 
	 * @return
	 */
	int getTotal();

	/**
	 * 每页数据个数
	 * 
	 * @return
	 */
	int getPageSize();

	/**
	 * 当前页码
	 * 
	 * @return
	 */
	int getPageIndex();

	/**
	 * 记录便宜量
	 * 
	 * @return
	 * */
	int getOffset();

}
