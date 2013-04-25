/**
 * 
 */
package benworks.comet.broadcast;

/**
 * @author benworks
 * 
 */
public class SerialUtils {
	private static ThreadLocal<Integer> serialHolder = new ThreadLocal<Integer>();

	public static void set(int serial) {
		serialHolder.set(serial);
	}

	public static int get() {
		Integer serial = serialHolder.get();
		if (serial == null)
			return 0;
		return serial;
	}

	public static void clear() {
		serialHolder.set(null);
	}
}
