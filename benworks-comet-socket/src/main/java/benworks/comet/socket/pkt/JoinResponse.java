package benworks.comet.socket.pkt;

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

import benworks.comet.socket.Constants;
import benworks.comet.socket.SessionStatus;

/**
 * 加入网络响应协议
 * 
 * @author benworks
 */
public class JoinResponse extends Packet implements PacketHandler {

	@Field(index = 0, name = "加入网络响应协议", catalog = "generic")
	public final static short PACKET_TYPE = 0x0021;

	@Field(index = 1, name = "额外信息，key=reason为错误信息")
	private Map<String, String> properties;
	@Field(index = 2, name = "接入结果")
	private int result;
	@Field(index = 3, name = "分配的节点ID。对于Service，返回的id和请求中的id相同")
	private int nodeId;
	@Field(index = 4, name = "返回节点当前的状态")
	private byte status;
	@Field(index = 5, name = "该连接映射的外网地址,格式: ‘ip:port’")
	private String externalAddress;
	@Field(index = 6, name = "Gate可能返回一份其他Gate列表. 每个Gate信息格式: ‘gid:ip:port’")
	private String[] gates;

	public JoinResponse() {
		super(PACKET_TYPE);
	}

	public JoinResponse(Map<String, String> properties, int result, int nodeId,
			byte status, String externalAddress, String[] gates) {
		super(PACKET_TYPE);
		this.properties = properties;
		this.result = result;
		this.nodeId = nodeId;
		this.status = status;
		this.externalAddress = externalAddress;
		this.gates = gates;
		length += mapLength(properties) + 9 + stringLength(externalAddress)
				+ stringArrayLength(gates);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		putMap(body, properties);
		body.putInt(result);
		body.putInt(nodeId);
		body.put(status);
		putString(body, externalAddress);
		putStringArray(body, gates);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.properties = readMap(buffer);
		this.result = buffer.getInt();
		this.nodeId = buffer.getInt();
		this.status = buffer.get();
		this.externalAddress = readString(buffer);
		this.gates = readStringArray(buffer);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public int getResult() {
		return result;
	}

	public int getNodeId() {
		return nodeId;
	}

	public byte getStatus() {
		return status;
	}

	public String getExternalAddress() {
		return externalAddress;
	}

	public String[] getGates() {
		return gates;
	}

	@Override
	public void handle(IoSession session) {
		session.setAttribute(Constants.SESSION_STATUS_KEY,
				SessionStatus.valueOf(getStatus()));
	}

}
