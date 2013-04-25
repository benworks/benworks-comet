package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * SET_PROPERTY_REQ应答
 * 
 * @author hebe.liu
 * 
 */
public class SetPropertyResponse extends Packet implements PacketHandler {
	@Field(index = 0, name = "SET_PROPERTY_REQ应答", catalog = "generic")
	public final static short PACKET_TYPE = 0x74;

	@Field(index = 1, name = "递增的序列号,应答中的serial和请求一致")
	private int serial;

	@Field(index = 2, name = "属性名")
	private String name;

	@Field(index = 3, name = "属性值。读取失败，返回空字符串")
	private String value;

	public SetPropertyResponse() {
		super(PACKET_TYPE);
	}

	public SetPropertyResponse(int serial, String name) {
		super(PACKET_TYPE);
		this.serial = serial;
		this.name = name;
		this.length += 4 + stringLength(name) + stringLength(value);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putInt(serial);
		putString(body, name);
		putString(body, value);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.serial = buffer.getInt();
		this.name = readString(buffer);
		this.value = readString(buffer);
	}

	public int getSerial() {
		return serial;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public void handle(IoSession session) {
	}
}
