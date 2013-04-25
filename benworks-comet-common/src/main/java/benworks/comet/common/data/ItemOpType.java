package benworks.comet.common.data;

import benworks.comet.common.utils.SpringUtils;

/**
 * 
 * @author benworks
 * 
 */
public class ItemOpType {

	private int id;
	private String code;
	private String name;
	private int operation;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * 物品操作代号
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
	 * 物品操作说明
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
	 * 操作类型，0为失去，1为得到，2为自身流转，3为其他
	 * 
	 * @return
	 */
	public int getOperation() {
		return operation;
	}

	public void setOperation(int operation) {
		this.operation = operation;
	}

	private static ItemOpType DEFAULT_iTEM_OP_TYPE = new ItemOpType() {
		public int getId() {
			return 0;
		}

		public void setId(int id) {
		}

		public String getCode() {
			return "UNKNOWN_ITEM_OP_TYPE";
		}

		public void setCode(String code) {
		}

		public String getName() {
			return getCode();
		}

		public void setName(String name) {
		}

		public int getOperation() {
			return 3;
		}

		public void setOperation(int operation) {
		}
	};

	public static ItemOpType get(int id) {
		StaticDataManager manager = SpringUtils
				.getBeanOfType(StaticDataManager.class);
		ItemOpType type = manager.getMap(ItemOpType.class, id);
		if (type == null) {
			System.err.println("#### ItemOpType not Find:" + id);
			type = DEFAULT_iTEM_OP_TYPE;
		}
		return type;
	}

}
