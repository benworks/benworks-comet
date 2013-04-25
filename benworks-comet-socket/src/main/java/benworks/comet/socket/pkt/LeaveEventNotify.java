package benworks.comet.socket.pkt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * 当Gate主动断开指定节点连接（如被踢掉）前，会发LEAVE_EVENT给该节点 LEAVE_EVENT结构：
 * -------------------------------------------------- 字段 类型 描述 result UINT32
 * 原因代码 reason STRING 原因描述 --------------------------------------------------
 * LEAVE_EVENT 0x23 G->S,G->C Client/Service被动断开收到的event(如被踢掉)
 * 
 * @author bin.liu
 * 
 */
public class LeaveEventNotify extends Packet implements PacketHandler {

	private final static Log log = LogFactory.getLog(LeaveEventNotify.class);
	@Field(index = 0, name = "被动断开收到的event", catalog = "generic")
	public final static short PACKET_TYPE = 0x23;
	@Field(index = 1, name = "原因代码")
	private int result;
	@Field(index = 2, name = "原因描述")
	private String reason;

	public LeaveEventNotify() {
		super(PACKET_TYPE);
	}

	public LeaveEventNotify(int result, String reason) {
		super(PACKET_TYPE);
		this.result = result;
		this.reason = reason;
		this.length += 4 + stringLength(reason);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putInt(result);
		putString(body, reason);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.result = buffer.getInt();
		this.reason = readString(buffer);
	}

	@Override
	public void handle(IoSession session) {
		log.info("Leave ha.net, Result : " + getResult() + " Reason: "
				+ getReason());
	}

	public int getResult() {
		return result;
	}

	public String getReason() {
		return reason;
	}

}
