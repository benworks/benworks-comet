/**
 * 
 */
package benworks.comet.common.utils;

import java.util.Random;

/**
 * @author benworks
 * 
 */
public class RandomUtils {

	private static final Random random = new Random(System.currentTimeMillis());

	private static Random[] randoms = new Random[10];

	static {
		for (int i = 0; i < randoms.length; i++) {
			randoms[i] = new Random(System.currentTimeMillis()
					+ random.nextInt(Integer.MAX_VALUE));
		}
	}

	public static int nextInt(int limit) {
		return random.nextInt();
	}

	public static int nextInt(int min, int max) {
		return nextInt2(min, max);
		// int value = max - min + 1;
		// return randdom.nextInt(value <= 0 ? 1 : value) + min;
	}

	private static int nextInt2(int min, int max) {
		int randomIndex = random.nextInt(randoms.length);
		Random random = randoms[randomIndex];
		int value = max - min + 1;
		return random.nextInt(value <= 0 ? 1 : value) + min;
	}

	public static boolean randomHit(int limit, int value) {
		if (value <= 0)
			return false;
		if (value >= limit)
			return true;
		int randomValue = nextInt2(1, limit);
		return randomValue <= value;
	}

}
