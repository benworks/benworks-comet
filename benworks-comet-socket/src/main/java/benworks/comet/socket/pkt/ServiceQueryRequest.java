/**
 * 
 */
package benworks.comet.socket.pkt;

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 查询服务所在节点
 * 
 * SERVICE_QUERY_REQ 0x33 S->G,C->G 从名字服务中根据服务类型查询指定的服务
 * ------------------------------------------------------ SERVICE_QUERY_REQ结构：
 * 字段 类型 描述 properties Properties 额外信息 service_type STRING 要查询的services的类型。
 * ------------------------------------------------------
 * 
 * @author binliu;
 * 
 */
public class ServiceQueryRequest extends Packet {
	@Field(index = 0, name = "版本协商", catalog = "generic")
	public final static short PACKET_TYPE = 0x33;
	@Field(index = 1, name = "额外信息")
	private Map<String, String> properties;
	@Field(index = 2, name = "要查询的services的类型。")
	private String serviceType = "";

	public ServiceQueryRequest() {
		super(PACKET_TYPE);
	}

	public ServiceQueryRequest(Map<String, String> properties,
			String serviceType) {
		super(PACKET_TYPE);
		this.properties = properties;
		this.serviceType = serviceType;
		length = mapLength(properties) + stringLength(serviceType);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.properties = readMap(buffer);
		this.serviceType = readString(buffer);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		putMap(body, properties);
		putString(body, serviceType);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public String getServiceType() {
		return serviceType;
	}

}
