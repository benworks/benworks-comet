/**
 * 
 */
package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Service更改指定Client节点的状态：Service发送STATE_UPDATE给Gate，Gate更改指定节点的状态。
 * 如果目的Client节点的状态被降为none(0)，Gate将断开和该Client的连接
 * ------------------------------------------------------ STATE_UPDATE结构： 字段 类型
 * 描述 id ClientID 被更改的Client节点的id state UINT8 新的状态 result UINT32 更改原因代码 reason
 * STRING 更改原因说明 ------------------------------------------------------
 * STATE_UPDATE 0x26 S->G,G->G Service请求Gate更改指定Clinet的State
 * 
 * @author binlau
 * 
 */
public class StateChangeEventRequest extends Packet {
	@Field(index = 0, name = "版本协商", catalog = "generic")
	public final static short PACKET_TYPE = 0x26;
	@Field(index = 1, name = "被更改的Client节点的id")
	private int clientId;
	@Field(index = 2, name = "新的状态")
	private short state;
	@Field(index = 3, name = "更改原因代码")
	private int result;
	@Field(index = 4, name = "更改原因说明")
	private String reason;

	public StateChangeEventRequest() {
		super(PACKET_TYPE);
	}

	public StateChangeEventRequest(int clientId, short state, int result,
			String reason) {
		super(PACKET_TYPE);
		this.clientId = clientId;
		this.state = state;
		this.result = result;
		this.reason = reason;
		length += 10 + stringLength(reason);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putInt(clientId);
		body.putShort(state);
		body.putInt(result);
		putString(body, reason);
	}

	public void fromBuffer(IoBuffer buffer) {
		return;
	}

	public int getClientId() {
		return clientId;
	}

	public short getState() {
		return state;
	}

	public int getResult() {
		return result;
	}

	public String getReason() {
		return reason;
	}

}
