package benworks.comet.socket;

/**
 * session事件，以位表示，位置从0开始，int总共可表示31个事件
 * 
 * @author benworks
 * 
 */
public enum SessionEvent {
	Leave(1 << 0);

	private int value;

	private SessionEvent(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public boolean containEvent(int eventValue) {
		return (eventValue & value) > 0;
	}

	public static SessionEvent valueOf(int status) {
		switch (status) {
		case 1:
			return Leave;
		default:
			return null;
		}
	}
}
