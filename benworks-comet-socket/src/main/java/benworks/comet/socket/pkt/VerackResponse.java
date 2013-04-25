package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * 版本协商
 * 
 * @author hebe.liu
 * 
 */
public class VerackResponse extends Packet implements PacketHandler {
	@Field(index = 0, name = "版本协议", catalog = "generic")
	public final static short PACKET_TYPE = 0x10;

	@Field(index = 1, name = "协议族( ‘xlands.ha2.internal’ )")
	private String family;

	@Field(index = 2, name = "自己当前的版本")
	private int currVersion;

	@Field(index = 3, name = "自己当前支持的最低版本")
	private int leastVersion;

	public VerackResponse() {
		super(PACKET_TYPE);
	}

	public VerackResponse(String family, int currVersion, int leastVersion) {
		super(PACKET_TYPE);
		this.family = family;
		this.currVersion = currVersion;
		this.leastVersion = leastVersion;
		this.length += stringLength(family) + 8;
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		putString(body, family);
		body.putInt(currVersion);
		body.putInt(leastVersion);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.family = readString(buffer);
		this.currVersion = buffer.getInt();
		this.leastVersion = buffer.getInt();
	}

	public String getFamily() {
		return family;
	}

	public long getCurrVersion() {
		return currVersion;
	}

	public long getLeastVersion() {
		return leastVersion;
	}

	@Override
	public void handle(IoSession session) {
		System.out.println(" VerackResponse  handle  收到了 收到了  版本协议");
	}

}
