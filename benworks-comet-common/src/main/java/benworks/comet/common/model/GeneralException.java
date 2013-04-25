package benworks.comet.common.model;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.exception.NestableRuntimeException;

import benworks.comet.common.data.ErrorCode;

/**
 * 异常的父类
 * 
 * @author benworks
 * 
 */
public class GeneralException extends NestableRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8842056020814582922L;

	/** 未知错误 */
	public static final int UNKNOWN = 0;

	/** 没有权限 */
	public static final int NO_PERMISSION = 1;

	/** 非法词语 */
	public static final int ILLEGAL_WORK = 2;

	private Map<String, Object> extraInfos;

	private int errorCode = 0;

	private String message;

	public GeneralException(int errorCode, Object... args) {
		ErrorCode ec = ErrorCode.get(errorCode);
		if (ec == null) {
			this.errorCode = 0;
		}
		this.errorCode = ec.getId();
		this.message = ec.getMessage();
		try {
			if (args != null && args.length > 0)
				this.message = MessageFormat.format(message, args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addExtraInfo(String key, Object value) {
		// 防止是value为自身引起死循环
		if (this.equals(value)) {
			return;
		}
		if (extraInfos == null) {
			extraInfos = new HashMap<String, Object>();
		}
		extraInfos.put(key, value);
	}

	public Map<String, Object> getExtraInfos() {
		return extraInfos;
	}

	/**
	 * 错误代号，对错误进行分类
	 * 
	 * @return
	 */
	public int getErrorCode() {
		return errorCode;
	}

	public String getMessage() {
		return message;
	}

}
