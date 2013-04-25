package benworks.comet.socket.pkt;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 协议包的抽象类，只要定义了协议头格式
 * 
 * @author benworks
 * 
 */
public abstract class Packet {
	// length(4)+srcId(4)+destId(4)+packetType(2)+ttl(1)+flags(1)=16
	@Field(index = 0, name = "packet格式头，所有协议都以此信息开头", catalog = "generic")
	public final static int PACKET_HEADER_LENGTH = 16;

	@Field(index = 1, name = "协议总长度，包括header")
	protected int length;
	@Field(index = 2, name = "发送源ID")
	private int srcId;
	@Field(index = 3, name = "目标ID")
	private int destId;
	@Field(index = 4, name = "协议类型")
	private short packetType;
	@Field(index = 5, name = "保留")
	private byte ttl;
	@Field(index = 6, name = "保留")
	private byte flags;

	// 是否允许缓存IoBuffer，在转发协议的场合，可以重用已经创建buffer，而不需要重新从创建协议
	protected boolean allowCacheBuffer = true;
	// 缓存的IoBuffer
	private IoBuffer cacheBuffer;

	public Packet() {
	}

	public Packet(short packetType) {
		this.packetType = packetType;
	}

	public IoBuffer toBuffer() {
		if (allowCacheBuffer && cacheBuffer != null) {
			return cacheBuffer.duplicate();
		}

		IoBuffer body = IoBuffer.allocate(length + PACKET_HEADER_LENGTH);
		toBuffer(body);
		body.flip();
		cacheBuffer = body;
		return body;
	}

	protected void toBuffer(IoBuffer body) {
		body.putInt(length + PACKET_HEADER_LENGTH);
		body.putInt(srcId);
		body.putInt(destId);
		body.putShort(packetType);
		body.put(ttl);
		body.put(flags);
	}

	public void fromBuffer(IoBuffer buffer) {
		length = buffer.getInt();
		srcId = buffer.getInt();
		destId = buffer.getInt();
		packetType = buffer.getShort();
		ttl = buffer.get();
		flags = buffer.get();
	}

	public int getLength() {
		return length;
	}

	public short getPacketType() {
		return packetType;
	}

	public int getSrcId() {
		return srcId;
	}

	public void setSrcId(int srcId) {
		this.srcId = srcId;
	}

	public int getDestId() {
		return destId;
	}

	public void setDestId(int destId) {
		this.destId = destId;
	}

	public byte getTtl() {
		return ttl;
	}

	public byte getFlags() {
		return flags;
	}

	/* ################################################ */
	public void putMap(IoBuffer buffer, Map<String, String> map) {
		if (map == null) {
			buffer.putInt(0);
			return;
		}
		buffer.putInt(map.size());
		for (Entry<String, String> entry : map.entrySet()) {
			putString(buffer, entry.getKey());
			putString(buffer, entry.getValue());
		}
	}

	public void putString(IoBuffer buffer, String s) {
		if (s == null) {
			buffer.putShort((short) 0);
			return;
		}
		buffer.putShort((short) s.length());
		buffer.put(s.getBytes());
	}

	public void putBytes(IoBuffer buffer, byte[] bytes) {
		if (bytes == null) {
			buffer.putInt(0);
			return;
		}
		buffer.putInt(bytes.length);
		buffer.put(bytes);
	}

	public void putStringArray(IoBuffer buffer, String[] strings) {
		if (strings == null || strings.length == 0) {
			buffer.putInt(0);
			return;
		}
		buffer.putInt(strings.length);
		for (String s : strings)
			putString(buffer, s);
	}

	public void putIntArray(IoBuffer buffer, int[] ints) {
		if (ints == null || ints.length == 0) {
			buffer.putInt(0);
			return;
		}
		buffer.putInt(ints.length);
		for (int value : ints)
			buffer.putInt(value);
	}

	public int[] readIntArray(IoBuffer buffer) {
		int size = buffer.getInt();
		int[] array = new int[size];
		for (int i = 0; i < size; i++) {
			array[i] = buffer.getInt();
		}

		return array;
	}

	public String[] readStringArray(IoBuffer buffer) {
		int size = buffer.getInt();
		String[] array = new String[size];
		for (int i = 0; i < size; i++) {
			array[i] = readString(buffer);
		}
		return array;
	}

	public byte[] readBytes(IoBuffer buffer) {
		int size = buffer.getInt();
		byte[] bytes = new byte[size];
		buffer.get(bytes);
		return bytes;
	}

	public Map<String, String> readMap(IoBuffer buffer) {
		int size = buffer.getInt();
		if (size == 0)
			return Collections.emptyMap();
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < size; i++) {
			String s = readString(buffer);
			String v = readString(buffer);
			map.put(s, v);
		}
		return map;
	}

	public String readString(IoBuffer buffer) {
		int size = buffer.getShort();
		if (size == 0)
			return "";
		try {
			return buffer
					.getString(size, Charset.defaultCharset().newDecoder());
		} catch (CharacterCodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	public int mapLength(Map<String, String> map) {
		int len = 4;
		if (map == null || map.size() == 0)
			return len;
		for (Entry<String, String> entry : map.entrySet()) {
			len += stringLength(entry.getKey());
			len += stringLength(entry.getValue());
		}
		return len;
	}

	public int stringLength(String value) {
		int len = 2;
		if (value != null)
			len += value.getBytes().length;
		return len;
	}

	public int bytesLength(byte[] bytes) {
		int len = 4;
		if (bytes != null)
			len += bytes.length;
		return len;
	}

	public int intArrayLength(int[] ints) {
		int len = 4;
		if (ints != null)
			len += 4 * ints.length;
		return len;
	}

	public int stringArrayLength(String[] strings) {
		int len = 4;
		if (strings == null)
			return len;
		for (String string : strings) {
			len += stringLength(string);
		}
		return len;
	}
}
