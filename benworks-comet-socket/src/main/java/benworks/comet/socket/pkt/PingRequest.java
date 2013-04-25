package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 测速请求协议
 * 
 * @author benworks
 */
public class PingRequest extends Packet {

	@Field(index = 0, name = "测速请求协议", catalog = "generic")
	public final static short PACKET_TYPE = 0x0005;

	@Field(index = 1, name = "序列号，这次测速的标记")
	private int serial;

	@Field(index = 2, name = "当前时间戳")
	private long timestamp;

	@Field(index = 3, name = "发送的数据")
	private byte[] data;

	public PingRequest() {
		super(PACKET_TYPE);
	}

	public PingRequest(int serial, long timestamp, byte[] data) {
		super(PACKET_TYPE);
		this.serial = serial;
		this.timestamp = timestamp;
		this.data = data;
		this.length += 12 + bytesLength(data);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putInt(serial);
		body.putLong(timestamp);
		putBytes(body, data);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.serial = buffer.getInt();
		this.timestamp = buffer.getLong();
		data = readBytes(buffer);
	}

	public int getSerial() {
		return serial;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public byte[] getData() {
		return data;
	}

}
