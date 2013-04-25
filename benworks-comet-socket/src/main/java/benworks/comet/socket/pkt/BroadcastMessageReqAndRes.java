package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * 广播消息（业务消息），到多个目的
 * 
 * @author hebe.liu
 * 
 */
public class BroadcastMessageReqAndRes extends Packet implements PacketHandler {
	@Field(index = 0, name = "广播消息（业务消息），到多个目的", catalog = "generic")
	public final static short PACKET_TYPE = 0x51;

	@Field(index = 1, name = "目的节点列表")
	private int[] ids;

	@Field(index = 2, name = "消息类型（bit集合）已经定义的类型(0-7 bits系统保留，不允许app定义):"
			+ "lossable  = 0x01  --该消息是否可靠，不可靠的消息在网络拥塞或处理瓶颈时会被丢弃")
	private int routeType;

	@Field(index = 3, name = "传送数据")
	private byte[] bytes;

	public BroadcastMessageReqAndRes() {
		super(PACKET_TYPE);
	}

	public BroadcastMessageReqAndRes(int[] ids, int routeType, byte[] bytes) {
		super(PACKET_TYPE);
		this.ids = ids;
		this.routeType = routeType;
		this.bytes = bytes;
		this.length += intArrayLength(ids) + 4 + bytesLength(bytes);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		putIntArray(body, ids);
		body.putInt(routeType);
		putBytes(body, bytes);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.ids = readIntArray(buffer);
		this.routeType = buffer.getInt();
		this.bytes = readBytes(buffer);
	}

	public int[] getIds() {
		return ids;
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
