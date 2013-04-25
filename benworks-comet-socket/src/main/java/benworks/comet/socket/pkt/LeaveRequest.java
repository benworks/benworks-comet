/**
 * 
 */
package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 节点离开xland.net：发出LEAVE请求，Gate收到LEAVE会断开连接 LEAVE结构：（LEAVE没有任何成员） LEAVE 0x22
 * C->G,S->G Client/Service离开Gate
 * 
 * @author bin.liu
 * 
 */
public class LeaveRequest extends Packet {
	@Field(index = 0, name = "节点离开xland.net", catalog = "generic")
	public final static short PACKET_TYPE = 0x22;

	public LeaveRequest() {
		super(PACKET_TYPE);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
	}
}
