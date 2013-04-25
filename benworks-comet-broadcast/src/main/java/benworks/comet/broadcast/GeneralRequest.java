package benworks.comet.broadcast;

import java.io.Serializable;

/**
 * 
 * @ClassName: GeneralRequest
 * @Description: 请求体
 * @author aben328@gmail.com
 * @date 2011-11-1 上午9:32:07
 * 
 */
public class GeneralRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5723841710649321934L;
	// 序列号
	private int serial;
	// 用户唯一标志
	private int sid;
	// 事件标志
	private String action;
	// 参数列表
	private Object[] params;

	public int getSerial() {
		return serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeneralRequest[action=" + action);
		builder.append(", serial=" + serial);
		builder.append(", sid=" + sid);
		builder.append(", params=" + params).append("]");
		return builder.toString();
	}
}
