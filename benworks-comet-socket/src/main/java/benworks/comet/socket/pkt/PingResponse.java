package benworks.comet.socket.pkt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * 测速响应协议
 * 
 * @author benworks
 */
public class PingResponse extends Packet implements PacketHandler {

	private final static Log log = LogFactory.getLog(PingResponse.class);

	@Field(index = 0, name = "测速响应协议", catalog = "generic")
	public final static short PACKET_TYPE = 0x06;

	@Field(index = 1, name = "同PingRequest")
	private int serial;

	@Field(index = 2, name = "同PingRequest")
	private long timestamp;

	@Field(index = 3, name = "同PingRequest")
	private byte[] data;

	public PingResponse() {
		super(PACKET_TYPE);
	}

	public PingResponse(int serial, long timestamp, byte[] data) {
		super(PACKET_TYPE);
		this.serial = serial;
		this.timestamp = timestamp;
		this.data = data;
		this.length += 8 + data.length;
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

	@Override
	public void handle(IoSession session) {
		log.info("Ping " + getSerial() + " Time "
				+ (System.currentTimeMillis() - getTimestamp()));
	}

}
