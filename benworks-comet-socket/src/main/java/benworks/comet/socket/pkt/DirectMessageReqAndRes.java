package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * 定向消息(业务消息)
 * 
 * @author hebe.liu
 * 
 */
public class DirectMessageReqAndRes extends Packet implements PacketHandler {

	@Field(index = 0, name = "定向消息（业务消息）", catalog = "generic")
	public final static short PACKET_TYPE = 0x50;

	@Field(index = 1, name = "目的节点")
	private int id;

	@Field(index = 2, name = "消息类型（bit集合）已经定义的类型(0-7 bits系统保留，不允许app定义):"
			+ "lossable  = 0x01  --该消息是否可靠，不可靠的消息在网络拥塞或处理瓶颈时会被丢弃")
	private int routeType;

	@Field(index = 3, name = "传送消息")
	private byte[] bytes;

	public DirectMessageReqAndRes() {
		super(PACKET_TYPE);
	}

	public DirectMessageReqAndRes(int id, int routeType, byte[] bytes) {
		super(PACKET_TYPE);
		this.id = id;
		this.routeType = routeType;
		this.bytes = bytes;
		this.length += 8 + bytesLength(bytes);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putInt(id);
		body.putInt(routeType);
		putBytes(body, bytes);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.id = buffer.getInt();
		this.routeType = buffer.getInt();
		this.bytes = readBytes(buffer);
	}

	public int getId() {
		return id;
	}

	public int getRouteType() {
		return routeType;
	}

	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public void handle(IoSession session) {

	}
}
