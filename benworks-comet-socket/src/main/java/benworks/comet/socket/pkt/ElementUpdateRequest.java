package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 更改广播组中节点信息
 * 
 * @author hebe.liu
 * 
 */
public class ElementUpdateRequest extends Packet {
	@Field(index = 0, name = "更改广播组中节点信息", catalog = "generic")
	public final static short PACKET_TYPE = 0x46;

	@Field(index = 1, name = "广播组ID")
	private long groupId;

	@Field(index = 2, name = "节点ID")
	private int nodes;

	@Field(index = 3, name = "更改send_filter方式: 0:add  1:remove  3:set ")
	private byte sendFilterOperation;

	@Field(index = 4, name = "更改的send_filter值")
	private int sendFilter;

	@Field(index = 5, name = "更改recv_filte方式: 0:add  1:remove  3:set")
	private byte recvFilterOperation;

	@Field(index = 6, name = "更改的recv_filter值")
	private int recvFilter;

	public ElementUpdateRequest() {
		super(PACKET_TYPE);
	}

	public ElementUpdateRequest(long groupId, int nodes,
			byte sendFilterOperation, int sendFilter, byte recvFilterOperation,
			int recvFilter) {
		super(PACKET_TYPE);
		this.groupId = groupId;
		this.nodes = nodes;
		this.sendFilterOperation = sendFilterOperation;
		this.sendFilter = sendFilter;
		this.recvFilterOperation = recvFilterOperation;
		this.recvFilter = recvFilter;
		this.length += 26;
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putLong(groupId);
		body.putInt(1);
		body.putInt(nodes);
		body.put(sendFilterOperation);
		body.putInt(sendFilter);
		body.put(recvFilterOperation);
		body.putInt(recvFilter);

	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.groupId = buffer.getLong();
		buffer.getInt();
		this.nodes = buffer.getInt();
		this.sendFilterOperation = buffer.get();
		this.sendFilter = buffer.getInt();
		this.recvFilterOperation = buffer.get();
		this.recvFilter = buffer.getInt();
	}

	public long getGroupId() {
		return groupId;
	}

	public int getNodes() {
		return nodes;
	}

	public byte getSendFilterOperation() {
		return sendFilterOperation;
	}

	public int getSendFilter() {
		return sendFilter;
	}

	public byte getRecvFilterOperation() {
		return recvFilterOperation;
	}

	public int getRecvFilter() {
		return recvFilter;
	}
}
