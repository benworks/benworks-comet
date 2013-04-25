package benworks.comet.common.data;

import benworks.comet.common.utils.SpringUtils;

/**
 * 
 * @author benworks
 * 
 */
public class ErrorCode {

	private int id;

	private String code;

	private String message;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 错误代号
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * 提示信息
	 * 
	 * @return
	 */
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static ErrorCode DEFAULT_ERROR_CODE = new ErrorCode() {
		public int getId() {
			return 0;
		}

		public void setId(int id) {
		}

		public String getCode() {
			return "UNKNOWN_ERROR";
		}

		public void setCode(String code) {
		}

		public String getMessage() {
			return "Unknown Error!";
		}

		public void setMessage(String message) {
		}
	};

	public static ErrorCode get(int id) {
		StaticDataManager manager = SpringUtils
				.getBeanOfType(StaticDataManager.class);
		ErrorCode ec = manager.getMap(ErrorCode.class, id);
		if (ec == null) {
			System.err.println("#### ErrorCode not Find:" + id);
			ec = DEFAULT_ERROR_CODE;
		}
		return ec;
	}
}
