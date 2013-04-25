package benworks.comet.socket;

/**
 * session状态，状态跟权限是相关的
 * 
 * @author benworks
 * 
 */
public enum SessionStatus {
	Connect((byte) 1), Join((byte) 2), Login((byte) 3), All((byte) 127);

	private byte permission;

	private SessionStatus(byte permission) {
		this.permission = permission;
	}

	public byte getPermission() {
		return permission;
	}

	public boolean hasPermission(SessionStatus status) {
		if (status == null)
			return true;
		return permission >= status.getPermission();
	}

	public boolean hasPermission(byte permission) {
		return this.permission >= permission;
	}

	public static SessionStatus valueOf(byte status) {
		switch (status) {
		case 1:
			return Connect;
		case 2:
			return Join;
		case 3:
			return Login;
		case 127:
			return All;
		default:
			return Connect;
		}
	}
}
