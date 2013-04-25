package benworks.comet.broadcast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import flex.messaging.io.SerializationContext;
import flex.messaging.io.amf.Amf3Input;
import flex.messaging.io.amf.Amf3Output;
import flex.messaging.io.amf.AmfTrace;

public class Amf3Utils {

	private final static SerializationContext serializationContext = new SerializationContext();
	static {
		serializationContext.legacyCollection = true;
	}

	private final static Map<String, int[]> statMap = new ConcurrentHashMap<String, int[]>();
	private static long lastStatMs = System.currentTimeMillis();
	private final static Log protocalLog = LogFactory
			.getLog("protocol-count.log");

	/**
	 * 将一个object转化成amf3格式的字节数组
	 * 
	 * @param obj
	 * @return
	 */
	public static byte[] getAmf3ByteArray(Object obj) {
		if (obj instanceof byte[]) {
			return (byte[]) obj;
		}
		try {
			Amf3Output amf3out = new Amf3Output(serializationContext);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			amf3out.setOutputStream(bos);
			amf3out.writeObject(obj);
			byte[] bs = bos.toByteArray();
			//
			String name = obj.getClass().getName();
			// System.out.println("******** Write " + name + " len:" +
			// bs.length);
			stat(name, bs.length);
			return bs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ArrayUtils.EMPTY_BYTE_ARRAY;
	}

	private static void stat(String name, int len) {
		int[] stats = statMap.get(name);
		if (stats == null) {
			stats = new int[2];
			statMap.put(name, stats);
		}
		stats[0]++;
		stats[1] += len;
		if (statMap.size() > 100
				|| System.currentTimeMillis() - lastStatMs > 60000) {
			ReentrantLock lock = new ReentrantLock();
			lock.lock();
			Map<String, int[]> cpMap = null;
			try {
				lastStatMs = System.currentTimeMillis();
				cpMap = new HashMap<String, int[]>(statMap);
				statMap.clear();
			} finally {
				lock.unlock();
			}
			if (cpMap != null && cpMap.size() > 0) {
				for (Entry<String, int[]> entry : cpMap.entrySet()) {
					int[] ss = entry.getValue();
					if (ss != null)
						protocalLog.info(entry.getKey() + "|" + ss[0] + "|"
								+ ss[1]);
				}
			}
		}
	}

	/**
	 * 将一个amf3格式字节数组转化成java对象
	 * 
	 * @param bs
	 * @return
	 */
	public static Object getObjectFromAmf3ByteArray(byte[] bs) {
		// System.out.println("********read len:"+bs.length);
		try {
			Amf3Input amf3in = new Amf3Input(serializationContext);
			amf3in.setDebugTrace(new AmfTrace());
			ByteArrayInputStream bis = new ByteArrayInputStream(bs);
			amf3in.setInputStream(bis);
			return amf3in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
