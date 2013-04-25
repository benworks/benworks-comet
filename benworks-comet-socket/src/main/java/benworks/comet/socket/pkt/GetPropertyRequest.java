package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 获取指定节点的指定属性
 * 
 * @author hebe.liu
 * 
 */
public class GetPropertyRequest extends Packet {
	@Field(index = 0, name = "获取指定节点的指定属性", catalog = "generic")
	public final static short PACKET_TYPE = 0x70;

	@Field(index = 1, name = "递增的序列号,应答中的serial和请求一致")
	private int serial;

	@Field(index = 2, name = "属性名")
	private String name;

	public GetPropertyRequest() {
		super(PACKET_TYPE);
	}

	public GetPropertyRequest(int serial, String name) {
		super(PACKET_TYPE);
		this.serial = serial;
		this.name = name;
		this.length += 4 + stringLength(name);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putInt(serial);
		putString(body, name);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.serial = buffer.getInt();
		this.name = readString(buffer);
	}

	public int getSerial() {
		return serial;
	}

	public String getName() {
		return name;
	}
}
