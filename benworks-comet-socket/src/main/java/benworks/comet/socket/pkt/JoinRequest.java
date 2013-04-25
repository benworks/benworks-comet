package benworks.comet.socket.pkt;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 加入网络请求协议
 * 
 * @author benworks
 */
public class JoinRequest extends Packet {

	@Field(index = 0, name = "加入网络请求协议", catalog = "generic")
	public final static short PACKET_TYPE = 0x0020;

	@Field(index = 1, name = "额外信息")
	private Map<String, String> properties;
	@Field(index = 2, name = "加入节点的id，service节点是固定id的")
	private int nodeId;
	@Field(index = 3, name = "加入节点的名字")
	private String name;
	@Field(index = 4, name = "节点的本地ip列表")
	private String[] localIps;
	@Field(index = 5, name = "节点的host id(唯一标识该节点主机)")
	private String hostId;

	public JoinRequest() {
		super(PACKET_TYPE);
	}

	public JoinRequest(Map<String, String> properties, int nodeId, String name,
			String[] localIps, String hostId) {
		super(PACKET_TYPE);
		this.properties = properties;
		this.nodeId = nodeId;
		this.name = name;
		this.localIps = localIps;
		this.hostId = hostId;
		length += mapLength(properties) + 4 + stringLength(name)
				+ stringArrayLength(localIps) + stringLength(hostId);
	}

	public static String[] getAllNoLoopbackAddresses() {
		try {
			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
					.getNetworkInterfaces();
			Collection<String> addresses = new ArrayList<String>();

			while (networkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = networkInterfaces
						.nextElement();
				Enumeration<InetAddress> inetAddresses = networkInterface
						.getInetAddresses();
				while (inetAddresses.hasMoreElements()) {
					InetAddress inetAddress = inetAddresses.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						addresses.add(inetAddress.getHostAddress());
					}
				}
			}
			String[] addressAry = new String[addresses.size()];
			return addresses.toArray(addressAry);
		} catch (SocketException e) {
			return null;
		}
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		putMap(body, properties);
		body.putInt(nodeId);
		putString(body, name);
		putStringArray(body, localIps);
		putString(body, hostId);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.properties = readMap(buffer);
		this.nodeId = buffer.getInt();
		this.name = readString(buffer);
		this.localIps = readStringArray(buffer);
		this.hostId = readString(buffer);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public int getNodeId() {
		return nodeId;
	}

	public String getName() {
		return name;
	}

	public String[] getLocalIps() {
		return localIps;
	}

	public String getHostId() {
		return hostId;
	}

}
