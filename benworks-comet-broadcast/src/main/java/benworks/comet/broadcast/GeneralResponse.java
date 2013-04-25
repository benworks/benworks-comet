package benworks.comet.broadcast;

import benworks.comet.broadcast.ResponseMessage;

public class GeneralResponse implements ResponseMessage {
	/**
	 * 
	 */
	private static final long serialVersionUID = -469297520965143719L;
	// 跟请求对应的序列号
	private int serial;

	public int getSerial() {
		return serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

}
