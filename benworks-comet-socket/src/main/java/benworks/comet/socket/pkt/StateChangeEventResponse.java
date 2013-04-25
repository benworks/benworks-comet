/**
 * 
 */
package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * 
 * 节点状态改变通知：当节点的状态更改后，Gate会发STATE_EVENT给状态被更改的节点
 * ------------------------------------------------------ STATE_EVENT结构： 字段 类型
 * 描述 state UINT8 新的状态 result UINT32 更改原因代码 reason STRING 更改原因说明
 * ------------------------------------------------------ STATE_EVENT 0x27
 * G->G,G->S,G->C 当Service/Client状态发生改变时收到来自Gate的通知
 * 
 * @author binliu@xlands_inc.com
 */
public class StateChangeEventResponse extends Packet implements PacketHandler {
	@Field(index = 0, name = "版本协商", catalog = "generic")
	public final static short PACKET_TYPE = 0x27;
	@Field(index = 1, name = "新的状态")
	private short state;
	@Field(index = 2, name = "更改原因代码")
	private int result;
	@Field(index = 3, name = "更改原因说明")
	private String reason;

	public StateChangeEventResponse() {
		super(PACKET_TYPE);
	}

	public StateChangeEventResponse(short state, int result, String reason) {
		super(PACKET_TYPE);
		this.state = state;
		this.result = result;
		this.reason = reason;
		length += 6 + stringLength(reason);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.state = buffer.getShort();
		this.result = buffer.getInt();
		this.reason = readString(buffer);
	}

	protected void toBuffer(IoBuffer body) {
		return;
	}

	public short getState() {
		return state;
	}

	public String getReason() {
		return reason;
	}

	public int getResult() {
		return result;
	}

	@Override
	public void handle(IoSession session) {
		// TODO Auto-generated method stub

	}

}
