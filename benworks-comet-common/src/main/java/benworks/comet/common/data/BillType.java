package benworks.comet.common.data;

import benworks.comet.common.utils.SpringUtils;

/**
 * 
 * @author benworks
 * 
 */
public class BillType {

	private int id;
	private String code;
	private String name;
	private int operation = 2;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 消费类型代号
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
	 * 消费类型说明
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 操作类型，0为支出，1为收入，2为其他
	 * 
	 * @return
	 */
	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	private static BillType DEFAULT_BILL_TYPE = new BillType() {
		public int getId() {
			return 0;
		}

		public void setId(int id) {
		}

		public String getCode() {
			return "UNKNOWN_BILL_TYPE";
		}

		public void setCode(String code) {
		}

		public String getName() {
			return getCode();
		}

		public void setName(String name) {
		}

		public int getOperation() {
			return 2;
		}

		public void setOperation(int operation) {
		}
	};

	public static BillType get(int id) {
		StaticDataManager manager = SpringUtils
				.getBeanOfType(StaticDataManager.class);
		BillType type = manager.getMap(BillType.class, id);
		if (type == null) {
			System.err.println("#### BillType not Find:" + id);
			type = DEFAULT_BILL_TYPE;
		}
		return type;
	}
}
