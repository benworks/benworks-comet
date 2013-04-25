/**
 * 
 */
package benworks.comet.socket.pkt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * 版本协商：在Client/Service和Gate创建加密通道成功后，立即通过VER交换协议版本信息，并检查版本是否符合。 VER结构：
 * -------------------------------------------------- 字段 类型 描述 family STRING 协议簇
 * ( ‘xlands.ha2.internal’ ) current_version UINT32 自己当前的版本 least_version UINT32
 * 自己当前支持的最低版本 -------------------------------------------------- VER 0x10
 * C<->G,S<->G,G<->G 版本协商
 * 
 * @author bin.liu
 * 
 */
public class VersionExchange extends Packet implements PacketHandler {
	private final static Log log = LogFactory.getLog(VersionExchange.class);

	@Field(index = 0, name = "版本协商", catalog = "generic")
	public final static short PACKET_TYPE = 0x10;
	@Field(index = 1, name = "协议簇 (‘xlands.ha2.internal’)")
	private String family = "xlands.ha2.internal";
	@Field(index = 2, name = "自己当前的版本")
	private int currentVersion;
	@Field(index = 3, name = "自己当前支持的最低版本")
	private int leastVersion;

	public VersionExchange() {
		super(PACKET_TYPE);
	}

	public VersionExchange(String family, int currentVersion, int leastVersion) {
		super(PACKET_TYPE);
		this.family = family;
		this.currentVersion = currentVersion;
		this.leastVersion = leastVersion;
		this.length += stringLength(family) + 8;
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		putString(body, family);
		body.putInt(currentVersion);
		body.putInt(leastVersion);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.family = readString(buffer);
		this.currentVersion = buffer.getInt();
		this.leastVersion = buffer.getInt();
	}

	public String getFamily() {
		return family;
	}

	public int getCurrentVersion() {
		return currentVersion;
	}

	public int getLeastVersion() {
		return leastVersion;
	}

	@Override
	public void handle(IoSession session) {
		log.info("Family : " + getFamily() + " CurrentVersion: "
				+ getCurrentVersion() + " LeastVersion: " + getLeastVersion());
	}

}
