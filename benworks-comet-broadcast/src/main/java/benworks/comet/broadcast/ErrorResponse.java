package benworks.comet.broadcast;

import java.util.Map;

import benworks.comet.common.data.ErrorCode;
import benworks.comet.common.model.GeneralException;

public class ErrorResponse extends GeneralResponse {

	private static final long serialVersionUID = 8607549930891302358L;
	private int errorCode;
	private int serialCode;
	private String message;
	private Map<String, Object> extraInfos;

	public ErrorResponse() {

	}

	public ErrorResponse(ErrorCode errorCode) {
		this.errorCode = errorCode.getId();
		this.message = errorCode.getMessage();
	}

	public ErrorResponse(GeneralException e) {
		this.errorCode = e.getErrorCode();
		this.message = e.getMessage();
		this.extraInfos = e.getExtraInfos();
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public int getSerialCode() {
		return serialCode;
	}

	public void setSerialCode(int serialCode) {
		this.serialCode = serialCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Object> getExtraInfos() {
		return extraInfos;
	}

	public void setExtraInfos(Map<String, Object> extraInfos) {
		this.extraInfos = extraInfos;
	}

	public String toString() {
		return "SerialCode:" + getSerialCode() + " ;ErrorCode:"
				+ getErrorCode() + " ;Message:" + getMessage();
	}
}
