package benworks.comet.socket.pkt;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * 打开加密通道响应的协议
 * 
 * @author benworks
 */
public class SslOpenResponse extends Packet implements PacketHandler {
	private final static Log log = LogFactory.getLog(SslOpenResponse.class);
	@Field(index = 0, name = "打开加密通道响应的协议", catalog = "generic")
	public final static short PACKET_TYPE = 0x0002;

	@Field(index = 1, name = "额外信息，['ca'] = 服务端证书，['reason']=原因")
	private Map<String, String> properties;
	@Field(index = 2, name = "加密结果")
	private int result;
	@Field(index = 3, name = "选中的对称加密算法")
	private int allowedEncryptMethod;
	@Field(index = 4, name = "生成的SessionKey，使用Client提供的public_key加密")
	private byte[] sessionkey;

	public SslOpenResponse() {
		super(PACKET_TYPE);
	}

	public SslOpenResponse(Map<String, String> properties, int result,
			int allowedEncryptMethod, byte[] sessionkey) {
		super(PACKET_TYPE);
		this.properties = properties;
		this.result = result;
		this.allowedEncryptMethod = allowedEncryptMethod;
		this.sessionkey = sessionkey;
		length += mapLength(properties) + 8 + bytesLength(sessionkey);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		putMap(body, properties);
		body.putInt(result);
		body.putInt(allowedEncryptMethod);
		putBytes(body, sessionkey);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		properties = readMap(buffer);
		result = buffer.getInt();
		allowedEncryptMethod = buffer.getInt();
		sessionkey = readBytes(buffer);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public int getResult() {
		return result;
	}

	public int getAllowedEncryptMethod() {
		return allowedEncryptMethod;
	}

	public byte[] getSessionkey() {
		return sessionkey;
	}

	@Override
	public void handle(IoSession session) {
		log.info("Result : " + getResult() + " allowedEncryptMethod: "
				+ getAllowedEncryptMethod());
	}

}
