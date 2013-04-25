package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 更改广播组
 * 
 * @author hebe.liu
 * 
 */
public class GroupUpdateRequest extends Packet {
	@Field(index = 0, name = "更改广播组", catalog = "generic")
	public final static short PACKET_TYPE = 0x43;

	@Field(index = 1, name = "要更改的组ID")
	private long groupId;

	@Field(index = 2, name = "更改filter方式:0:add  1:remove  3:set ")
	private short operation;

	@Field(index = 3, name = "更改filter的值")
	private int filter;

	public GroupUpdateRequest() {
		super(PACKET_TYPE);
	}

	public GroupUpdateRequest(long groupId, short operation, int filter) {
		super(PACKET_TYPE);
		this.groupId = groupId;
		this.operation = operation;
		this.filter = filter;
		this.length += 14;
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putLong(groupId);
		body.putShort(operation);
		body.putInt(filter);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.groupId = buffer.getLong();
		this.operation = buffer.getShort();
		this.filter = buffer.getInt();
	}

	public long getGroupId() {
		return groupId;
	}

	public short getOperation() {
		return operation;
	}

	public int getFilter() {
		return filter;
	}
}
