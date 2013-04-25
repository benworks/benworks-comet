package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Service监控指定Client/Service节点事件，当节点事件触发，监控者会收到NODE_EVENT
 * 
 * SUBSCRIBE_NODE_EVENTS结构： --------------------------------------------------
 * 字段 类型 描述 id NodeID 被监控的节点ID events UINT32
 * 要监控的事件集合(覆盖方式，如果数组长度为０，则取消对该节点的所有监控) 目前支持的事件： 0x01 : 当节点和Gate连接主动断开时触发(leave)
 * 未来也不会有太多的event,用UINT32（最多３１种event）表示应该够了
 * --------------------------------------------------
 * 
 * SUBSCRIBE_NODE_EVENTS 0x28 S=>G,G->G 请求监控指定节点的指定事件
 * 
 * @author binliu
 */
public class SubscribeNodeEvent extends Packet {
	// private final static Log log =
	// LogFactory.getLog(SubscribeNodeEvent.class);
	@Field(index = 0, name = "版本协商", catalog = "generic")
	public final static short PACKET_TYPE = 0x28;
	@Field(index = 1, name = "被监控的节点ID")
	private int nodeId;
	@Field(index = 2, name = "要监控的事件集合，待定义")
	private int events;

	public SubscribeNodeEvent() {
		super(PACKET_TYPE);
	}

	public SubscribeNodeEvent(int nodeId, int events) {
		super(PACKET_TYPE);
		this.nodeId = nodeId;
		this.events = events;
		this.length += 8;
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putInt(nodeId);
		body.putInt(events);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.nodeId = buffer.getInt();
		this.events = buffer.getInt();
	}

	public int getNodeId() {
		return nodeId;
	}

	public int getEvents() {
		return events;
	}

}
