package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 从广播组中删除节点 = 0x45;
 * 
 * @author hebe.liu
 * 
 */
public class ElementLeaveRequest extends Packet {
	@Field(index = 0, name = "从广播组中删除节点", catalog = "generic")
	public final static short PACKET_TYPE = 0x45;

	@Field(index = 1, name = "广播组Id")
	private long groupId;

	@Field(index = 2, name = "节点ID")
	private int nodes;

	public ElementLeaveRequest() {
		super(PACKET_TYPE);
	}

	public ElementLeaveRequest(long groupId, int nodes) {
		super(PACKET_TYPE);
		this.groupId = groupId;
		this.nodes = nodes;
		this.length += 12;
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putLong(groupId);
		body.putInt(nodes);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.groupId = buffer.getLong();
		this.nodes = buffer.getInt();
	}

	public long getGroupId() {
		return groupId;
	}

	public int getNodes() {
		return nodes;
	}

}
