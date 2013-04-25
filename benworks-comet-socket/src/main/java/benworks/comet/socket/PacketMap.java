package benworks.comet.socket;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;

import benworks.comet.socket.pkt.Packet;
import benworks.comet.socket.utils.PackageUtils;

/**
 * 所有packet的映射列表
 * 
 * @author benworks
 * 
 */

public class PacketMap {
	private final static Log log = LogFactory.getLog(PacketMap.class);
	private static Map<Short, Class<? extends Packet>> packetClassMap = new HashMap<Short, Class<? extends Packet>>();

	static {
		reset();
	}

	/**
	 * 重置packet映射列表
	 */
	@SuppressWarnings("unchecked")
	public static void reset() {
		packetClassMap.clear();
		String[] allPacketClass = PackageUtils
				.findClassesInPackage(Packet.class.getPackage().getName()
						+ ".*");
		for (String className : allPacketClass) {
			try {
				Class<?> clazz = Class.forName(className);
				if (clazz.isInterface()
						|| Modifier.isAbstract(clazz.getModifiers()))
					continue;
				if (!Packet.class.isAssignableFrom(clazz))
					continue;
				Field field = clazz.getField("PACKET_TYPE");
				if (field == null)
					continue;
				if (!field.isAccessible())
					field.setAccessible(true);
				short packetType = field.getShort(null);
				// 后者覆盖前者
				Class<? extends Packet> hasMapClass = packetClassMap
						.get(packetType);
				if (hasMapClass != null) {
					log.info("#### " + packetType + " has bind to "
							+ hasMapClass.getName() + ",will replace with "
							+ className);
				}
				packetClassMap.put(packetType, (Class<? extends Packet>) clazz);
				log.info("#### " + packetType + " bind to " + className);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获得所有注册的packet class
	 * 
	 * @return 一个不允许修改的map
	 */
	public static Map<Short, Class<? extends Packet>> getAllPacketClasses() {
		return Collections.unmodifiableMap(packetClassMap);
	}

	public static Class<? extends Packet> getPacketClass(short packetType) {
		return packetClassMap.get(packetType);
	}

	public static Packet getPacket(short packetType, IoBuffer buffer) {
		return getPacket(getPacketClass(packetType), buffer);
	}

	public static <T extends Packet> T getPacket(Class<T> clazz, IoBuffer buffer) {
		if (clazz == null || buffer == null)
			return null;
		T instance = null;
		try {
			instance = clazz.newInstance();
			instance.fromBuffer(buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instance;
	}
}
