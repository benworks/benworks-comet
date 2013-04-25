package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 删除广播组 public final static short GROUPS_REMOVE = 0x42;
 * 
 * @author hebe.liu
 * 
 */
public class GroupRemoveRequest extends Packet {
	@Field(index = 0, name = "删除广播组", catalog = "generic")
	public final static short PACKET_TYPE = 0x42;

	@Field(index = 1, name = "广播组List")
	private long groups;

	public GroupRemoveRequest() {
		super(PACKET_TYPE);
	}

	public GroupRemoveRequest(long groups) {
		super(PACKET_TYPE);
		this.groups = groups;
		this.length += 8;
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putLong(groups);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.groups = buffer.getLong();
	}

	public long getGroups() {
		return groups;
	}
}
