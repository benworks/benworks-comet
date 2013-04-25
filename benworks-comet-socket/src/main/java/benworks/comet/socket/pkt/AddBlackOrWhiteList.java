package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 限制指定IP/主机连接Gate：Service广播ADD_BLOCK_ALLOW_LIST给所有的Gates
 * -------------------------------------------------- 字段 类型 描述 type UINT8 名单类型：
 * type=1 ip type=2 host blocks Array<STRING> 要添加阻止的ip ／host列表 Allows
 * Array<STRING> 要添加允许的ip / host列表
 * -------------------------------------------------- 添加黑白名单
 * ADD_BLOCK_ALLOW_LIST 0x60 S=>GS G->G
 * 
 * @author bin.liu
 * 
 */
public class AddBlackOrWhiteList extends Packet {
	@Field(index = 0, name = "添加黑白名单", catalog = "generic")
	public final static short PACKET_TYPE = 0x60;
	@Field(index = 1, name = "名单类型：type=1 ip,type=2 host")
	private byte type;
	@Field(index = 2, name = "黑名单")
	private String[] blackList;
	@Field(index = 3, name = "白名单")
	private String[] whiteList;

	public AddBlackOrWhiteList() {
		super(PACKET_TYPE);
	}

	public AddBlackOrWhiteList(byte type, String[] blackList, String[] whiteList) {
		super(PACKET_TYPE);
		this.type = type;
		this.blackList = blackList;
		this.whiteList = whiteList;
		this.length += 1 + stringArrayLength(blackList)
				+ stringArrayLength(whiteList);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.put(type);
		putStringArray(body, blackList);
		putStringArray(body, whiteList);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.type = buffer.get();
		this.blackList = readStringArray(buffer);
		this.whiteList = readStringArray(buffer);
	}

	public byte getType() {
		return type;
	}

	public String[] getBlackList() {
		return blackList;
	}

	public String[] getWhiteList() {
		return whiteList;
	}

}
