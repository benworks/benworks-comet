package benworks.comet.common.utils;

/**
 * 使用时间+机器ID+序列号方式生成唯一id From Twitter
 */
public class Snowflake {

	// Tue Nov 02 16:46:44 CST 2010 开始使用这个生成方式的时候,不要超过上线时间
	private static final long usingEpoch = 1288687604476L;

	private static final int machineIdBits = 10;
	private static final int maxMachineId = -1 ^ (-1 << machineIdBits);
	private static final int sequenceBits = 12;
	private static final int timestampLeftShift = sequenceBits + machineIdBits;
	private static final int machineIdShift = sequenceBits;
	private static final int sequenceMask = -1 ^ (-1 << sequenceBits);

	private volatile int sequence = 0;
	private volatile long lastTimeStamp;

	private final int machineId;

	public Snowflake(int mid) {
		this.machineId = mid;
		if (machineId > maxMachineId || machineId < 0) {
			throw new IllegalArgumentException(String.format(
					"machine Id can't be greater than %d or less than 0",
					maxMachineId));
		}
	}

	public synchronized long genId() {
		long timestamp = System.currentTimeMillis();
		if (lastTimeStamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0) // had been get 4096 times at this ms...
				timestamp = loopUntilNextMs(lastTimeStamp);
		} else {
			sequence = 0; // reset seq every ms
		}
		lastTimeStamp = timestamp;
		return ((timestamp - usingEpoch) << timestampLeftShift)
				| (machineId << machineIdShift) | sequence;
	}

	private long loopUntilNextMs(long timestamp) {
		long now = System.currentTimeMillis();
		while (now == timestamp)
			now = System.currentTimeMillis();
		return now;
	}
}
