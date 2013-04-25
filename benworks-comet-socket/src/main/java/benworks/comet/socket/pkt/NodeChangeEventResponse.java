/**
 * 
 */
package benworks.comet.socket.pkt;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * NODE_EVENT 0x29 G=>S，G->G,G->S 当指定节点指定事件触发，监控者收到的通知
 * 
 * NODE_EVENT结构： -------------------------------------------------- 字段 类型 描述
 * properties Properties 额外信息 id NodeID 被监控的节点ID events UINT32 被触发的事件集： 目前支持事件：
 * 0x01 : 当节点和Gate连接主动断开时触发(leave)
 * --------------------------------------------------
 * 
 * @author bin.liu
 * 
 */
public class NodeChangeEventResponse extends Packet implements PacketHandler {
	private final static Log log = LogFactory
			.getLog(NodeChangeEventResponse.class);
	@Field(index = 0, name = "当监听的节点发生事件变化时，会收到该通知", catalog = "generic")
	public final static short PACKET_TYPE = 0x29;
	@Field(index = 1, name = "额外信息")
	private Map<String, String> properties;
	@Field(index = 2, name = "节点号")
	private int nodeId;
	@Field(index = 3, name = "监听的事件类型集合(最多扩展32个事件应该足够)")
	private int events;

	public NodeChangeEventResponse() {
		super(PACKET_TYPE);
	}

	public NodeChangeEventResponse(Map<String, String> properties, int nodeId,
			int events) {
		super(PACKET_TYPE);
		this.properties = properties;
		this.nodeId = nodeId;
		this.events = events;
		length = mapLength(properties) + 8;
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.properties = readMap(buffer);
		this.nodeId = buffer.getInt();
		this.events = buffer.getInt();
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		putMap(body, properties);
		body.putInt(nodeId);
		body.putInt(nodeId);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public int getNodeId() {
		return nodeId;
	}

	public int getEvents() {
		return events;
	}

	@Override
	public void handle(IoSession session) {
		log.info("NodeId : " + getNodeId() + " Events: " + getEvents());
	}

}
