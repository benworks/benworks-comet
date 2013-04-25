/**
 * 
 */
package benworks.comet.socket.pkt;

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * SERVICE_QUERY_RES 0x34 G->S,G->C 返回查询的服务信息
 * 
 * SERVICE_QUERY_RES结构： ------------------------------------------------------
 * 字段 类型 描述 properties Properties 额外信息[‘resaon’] result UINT32
 * 结果，如果不为SUCCESS，则properties[‘reason’]指明原因 services Array<ServiceInfo>
 * 查询返回的service信息列表 ------------------------------------------------------
 * 
 * @author binliu
 * 
 */
public class ServiceQueryResponse extends Packet implements PacketHandler {
	@Field(index = 0, name = "版本协商", catalog = "generic")
	public final static short PACKET_TYPE = 0x34;
	@Field(index = 1, name = "额外信息[‘resaon’]")
	private Map<String, String> properties;
	@Field(index = 2, name = "结果，如果不为SUCCESS，则properties[‘reason’]指明原因")
	private int result;
	@Field(index = 3, name = "查询返回的service信息列表")
	private ServiceInfo[] services;

	public ServiceQueryResponse() {
		super(PACKET_TYPE);
	}

	public ServiceQueryResponse(Map<String, String> properties, int result,
			ServiceInfo[] services) {
		super(PACKET_TYPE);
		this.properties = properties;
		this.result = result;
		this.services = services;
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		properties = readMap(buffer);
		result = buffer.getInt();
		int size = buffer.getInt();
		services = new ServiceInfo[size];
		for (int i = 0; i < size; i++) {
			services[i].setProperties(readMap(buffer));
			services[i].setType(readString(buffer));
			services[i].setId(buffer.getInt());
			services[i].setName(readString(buffer));
			services[i].setVersion(buffer.getInt());
			services[i].setLeastVersion(buffer.getInt());
			services[i].setHighestVersion(buffer.getInt());
			services[i].setAccess(buffer.getInt());
			services[i].setQuery(buffer.getInt());
			services[i].setDoc(readString(buffer));
		}
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		putMap(body, properties);
		body.putInt(result);
		int size = services.length;
		body.putInt(size);
		for (ServiceInfo serviceInfo : services) {
			putMap(body, serviceInfo.getProperties());
			putString(body, serviceInfo.getType());
			body.putInt(serviceInfo.getId());
			putString(body, serviceInfo.getName());
			body.putInt(serviceInfo.getVersion());
			body.putInt(serviceInfo.getLeastVersion());
			body.putInt(serviceInfo.getHighestVersion());
			body.putInt(serviceInfo.getAccess());
			body.putInt(serviceInfo.getQuery());
			putString(body, serviceInfo.getDoc());
		}
	}

	@Override
	public void handle(IoSession session) {

	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public int getResult() {
		return result;
	}

	public ServiceInfo[] getServices() {
		return services;
	}

}
