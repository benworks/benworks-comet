package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * 组播消息
 * 
 * @author hebe.liu
 * 
 */
public class GroupMessageReqAndRes extends Packet implements PacketHandler {

	@Field(index = 0, name = "组播消息：Service发送GROUP_MESSAGE给指定的消息组。Gate会广播到组中每个节点（发送group message，group状态由service维护）", catalog = "generic")
	public final static short PACKET_TYPE = 0x52;

	@Field(index = 1, name = "广播组ID")
	private long gid;

	@Field(index = 2, name = "消息类型（bit集合)已经定义的类型(0-7 bits系统保留，不允许app定义):"
			+ "lossable  = 0x01  --该消息是否可靠，不可靠的消息在网络拥塞或处理瓶颈时会被丢弃"
			+ "App允许自定义8-31bit,作为消息组播消息过滤条件 ")
	private int routeType;

	@Field(index = 3, name = "忽略的目的节点集合(消息不会发给这些节点，即使它们在这个group中)")
	private int[] excludeIds;

	@Field(index = 4, name = "传送数据")
	private byte[] bytes;

	public GroupMessageReqAndRes() {
		super(PACKET_TYPE);
	}

	public GroupMessageReqAndRes(long gid, int[] excludeIds, byte[] bytes,
			int routeType) {
		super(PACKET_TYPE);
		this.gid = gid;
		this.routeType = routeType;
		this.excludeIds = excludeIds;
		this.bytes = bytes;
		this.length += 12 + intArrayLength(excludeIds) + bytesLength(bytes);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putLong(gid);
		body.putInt(routeType);
		putIntArray(body, excludeIds);
		putBytes(body, bytes);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.gid = buffer.getLong();
		this.routeType = buffer.getInt();
		readIntArray(buffer);
		this.bytes = readBytes(buffer);
	}

	public long getGid() {
		return gid;
	}

	public long getRouteType() {
		return routeType;
	}

	public int[] getExcludeIds() {
		return excludeIds;
	}

	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public void handle(IoSession session) {
	}
}
