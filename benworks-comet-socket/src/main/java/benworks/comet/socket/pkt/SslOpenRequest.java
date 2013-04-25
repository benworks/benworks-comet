package benworks.comet.socket.pkt;

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 打开加密通道请求的协议
 * 
 * @author benworks
 */
public class SslOpenRequest extends Packet {

	@Field(index = 0, name = "打开加密通道请求的协议", catalog = "generic")
	public final static short PACKET_TYPE = 0x0001;

	@Field(index = 1, name = "额外信息，['ca'] = 服务端证书")
	private Map<String, String> properties;
	@Field(index = 2, name = "支持的对称加密算法")
	private int[] supportMethods;
	@Field(index = 3, name = "提供的非对称加密算法")
	private int allowedMethod;
	@Field(index = 4, name = "提供的非对称加密算法的public key")
	private byte[] publickey;

	public SslOpenRequest() {
		super(PACKET_TYPE);
	}

	public SslOpenRequest(Map<String, String> properties, int[] supportMethods,
			int allowedMethod, byte[] publickey) {
		super(PACKET_TYPE);
		this.properties = properties;
		this.supportMethods = supportMethods;
		this.allowedMethod = allowedMethod;
		this.publickey = publickey;
		length = mapLength(properties) + intArrayLength(supportMethods) + 4
				+ bytesLength(publickey);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		putMap(body, properties);
		putIntArray(body, supportMethods);
		body.putInt(allowedMethod);
		putBytes(body, publickey);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.properties = readMap(buffer);
		this.supportMethods = readIntArray(buffer);
		this.allowedMethod = buffer.getInt();
		this.publickey = readBytes(buffer);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public int[] getSupportMethods() {
		return supportMethods;
	}

	public int getAllowedMethod() {
		return allowedMethod;
	}

	public byte[] getPublickey() {
		return publickey;
	}

}
