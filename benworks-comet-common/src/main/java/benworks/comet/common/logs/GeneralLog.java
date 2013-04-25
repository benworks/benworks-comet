/**
 * 
 */
package benworks.comet.common.logs;

/**
 * 输出日志对象接口，利用toString方法实现字符串延迟拼接
 * 
 * @author benworks
 * 
 */
public abstract class GeneralLog {
	/**
	 * 输出格式，以|分隔
	 */
	public final static String SEPARATOR = "|";

	public abstract String format();

	public final String toString() {
		return format();
	}
}
