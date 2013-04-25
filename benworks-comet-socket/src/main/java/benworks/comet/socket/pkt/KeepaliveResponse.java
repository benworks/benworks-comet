package benworks.comet.socket.pkt;

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

public class KeepaliveResponse extends Packet implements PacketHandler {

	@Field(index = 0, name = "Client/Service和Gate之间，以及Gate和Gate之间的心跳 协议", catalog = "generic")
	public final static short PACKET_TYPE = 0x25;

	@Field(index = 1, name = "额外信息")
	private Map<String, String> map;

	@Field(index = 2, name = "当前时间戳")
	private long timestamp;

	public KeepaliveResponse() {
		super(PACKET_TYPE);
	}

	public KeepaliveResponse(Map<String, String> map, Long timestamp) {
		super(PACKET_TYPE);
		this.map = map;
		this.timestamp = timestamp;
		this.length += mapLength(map) + 8;
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		putMap(body, map);
		body.putLong(timestamp);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		map = readMap(buffer);
		timestamp = buffer.getLong();
	}

	public Map<String, String> getMap() {
		return map;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	@Override
	public void handle(IoSession session) {

	}
}
