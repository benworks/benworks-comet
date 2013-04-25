/**
 * 
 */
package benworks.comet.socket.pkt;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 
 * Service向Gates发布自己：广播SERVICE_PUBLISH给所有Gates SERVICE_PUBLISH结构： 字段 类型 描述 info
 * ServiceInfo 需要发布的服务信息(必须是自己的)
 * 
 * SERVICE_PUBLISH 0x30 S=>GS Service注册自己到名字服务
 * 
 * service把自己的信息发布到gate上面，从而让其他node获得访问该service的能力
 * 字段type由服务器和客户端自相协商，一个type的例子："/xlands/account"
 * 
 * @author binliu
 * 
 */
public class ServicePublishRequest extends Packet {
	// private final static Log log =
	// LogFactory.getLog(ServicePublishRequest.class);
	@Field(index = 0, name = "版本协商", catalog = "generic")
	public final static short PACKET_TYPE = 0x30;
	@Field(index = 1, name = "额外信息")
	private Map<String, String> properties;
	@Field(index = 2, name = "Service的类型。< 需求2.6.1中’service_type’说明>)")
	private String type = "";
	@Field(index = 3, name = "Service的ID")
	private int id;
	@Field(index = 4, name = "该Service名字。< 需求 2.6.1中’ service_name’>")
	private String name;
	@Field(index = 5, name = "当前版本")
	private int version;
	@Field(index = 6, name = "支持客户端最低版本")
	private int leastVersion;
	@Field(index = 7, name = "支持客户端最高版本 ")
	private int highestVersion;
	@Field(index = 8, name = "访问权限，只有当node的state大于等于本服务的访问权限时才能访问该服务")
	private int access;
	@Field(index = 9, name = "只有node的state大于等于本服务的查询权限时才能查好像到该服务的信息")
	private int query;
	@Field(index = 10, name = "服务描述")
	private String doc = "";

	public ServicePublishRequest() {
		super(PACKET_TYPE);
	}

	public ServicePublishRequest(int id, String type, String name, String doc) {
		super(PACKET_TYPE);
		this.properties = new HashMap<String, String>();
		this.type = type;
		this.id = id;
		this.name = name;
		this.version = 0;
		this.leastVersion = 0;
		this.highestVersion = 0;
		this.access = 0;
		this.query = 0;
		this.doc = doc;
		length = mapLength(properties) + stringLength(type) + 4
				+ stringLength(name) + 20 + stringLength(doc);
	}

	public ServicePublishRequest(Map<String, String> properties, String type,
			int id, String name, int version, int leastVersion,
			int highestVersion, int access, int query, String doc) {
		super(PACKET_TYPE);
		this.properties = properties;
		this.type = type;
		this.id = id;
		this.name = name;
		this.version = version;
		this.leastVersion = leastVersion;
		this.highestVersion = highestVersion;
		this.access = access;
		this.query = query;
		this.doc = doc;
		length = mapLength(properties) + stringLength(type) + 4
				+ stringLength(name) + 20 + stringLength(doc);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.properties = readMap(buffer);
		this.type = readString(buffer);
		this.id = buffer.getInt();
		this.name = readString(buffer);
		this.version = buffer.getInt();
		this.leastVersion = buffer.getInt();
		this.highestVersion = buffer.getInt();
		this.access = buffer.getInt();
		this.query = buffer.getInt();
		this.doc = readString(buffer);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		putMap(body, properties);
		putString(body, type);
		body.putInt(id);
		putString(body, name);
		body.putInt(version);
		body.putInt(leastVersion);
		body.putInt(highestVersion);
		body.putInt(access);
		body.putInt(query);
		putString(body, doc);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public String getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getVersion() {
		return version;
	}

	public int getLeastVersion() {
		return leastVersion;
	}

	public int getHighestVersion() {
		return highestVersion;
	}

	public int getAccess() {
		return access;
	}

	public int getQuery() {
		return query;
	}

	public String getDoc() {
		return doc;
	}

}
