package benworks.comet.socket.pkt;

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 添加节点到广播组
 * 
 * @author hebe.liu
 * 
 */
public class ElementJoinRequest extends Packet {
	@Field(index = 0, name = "添加节点到广播组", catalog = "generic")
	public final static short PACKET_TYPE = 0x44;

	@Field(index = 1, name = "组ID")
	private long groupId;

	@Field(index = 2, name = "element的容量:默认1;实为旧协议 ElementInfo[] elements.length,而旧协议中此Length永远只为1;此字段目的为占位")
	private int elementsLength = 1;

	@Field(index = 3, name = "额外属性")
	private Map<String, String> properties;

	@Field(index = 4, name = "该节点的ID")
	private int id;

	@Field(index = 5, name = "该节点名")
	private String name;

	@Field(index = 6, name = "发送消息过滤设置。通过bit掩码方式过滤")
	private int sendFilter;

	@Field(index = 7, name = "接收消息过滤设置。通过bit掩码方式过滤")
	private int recvFilter;

	public ElementJoinRequest() {
		super(PACKET_TYPE);
	}

	public ElementJoinRequest(long groupId, Map<String, String> properties,
			int id, String name, int sendFilter, int recvFilter) {
		super(PACKET_TYPE);
		this.groupId = groupId;
		this.properties = properties;
		this.id = id;
		this.name = name;
		this.sendFilter = sendFilter;
		this.recvFilter = recvFilter;
		this.length += 8 + mapLength(properties) + 4 + stringLength(name) + 12;
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putLong(groupId);
		body.putInt(elementsLength);
		putMap(body, properties);
		body.putInt(id);
		putString(body, name);
		body.putInt(sendFilter);
		body.putInt(recvFilter);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.groupId = buffer.getLong();
		this.elementsLength = buffer.getInt();
		this.properties = readMap(buffer);
		this.id = buffer.getInt();
		this.name = readString(buffer);
		this.sendFilter = buffer.getInt();
		this.recvFilter = buffer.getInt();
	}

	public long getGroupId() {
		return groupId;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getSendFilter() {
		return sendFilter;
	}

	public int getRecvFilter() {
		return recvFilter;
	}

}
