package benworks.comet.socket.pkt;

import java.util.Map;

/**
 * 名字服务 数据结构ServiceInfo描述在名字服务中一个Service对象的信息
 * 
 * @author binliu
 * 
 */
public class ServiceInfo {

	@Field(index = 1, name = "额外信息")
	Map<String, String> properties;
	@Field(index = 2, name = "描述该service对象(实例)，用作client识别该服务对象，起名方式由应用逻辑自己协商")
	String type;
	@Field(index = 3, name = "Service的ID")
	int id;
	@Field(index = 4, name = "该Service名字。<需求 2.6.1中’ service_name’>")
	String name;
	@Field(index = 5, name = "当前版本")
	int version;
	@Field(index = 6, name = "支持客户端最低版本")
	int leastVersion;
	@Field(index = 7, name = "支持客户端最高版本 ")
	int highestVersion;
	@Field(index = 8, name = "访问权限，只有当node的state大于等于本服务的访问权限时才能访问该服务")
	int access;
	@Field(index = 9, name = "只有node的state大于等于本服务的查询权限时才能查好像到该服务的信息")
	int query;
	@Field(index = 10, name = "服务描述")
	String doc;

	public Map<String, String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getLeastVersion() {
		return leastVersion;
	}

	public void setLeastVersion(int leastVersion) {
		this.leastVersion = leastVersion;
	}

	public int getHighestVersion() {
		return highestVersion;
	}

	public void setHighestVersion(int highestVersion) {
		this.highestVersion = highestVersion;
	}

	public int getAccess() {
		return access;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	public int getQuery() {
		return query;
	}

	public void setQuery(int query) {
		this.query = query;
	}

	public String getDoc() {
		return doc;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}

	@Override
	public String toString() {
		return "ServiceInfo [properties=" + properties + ", type=" + type
				+ ", id=" + id + ", name=" + name + ", version=" + version
				+ ", leastVersion=" + leastVersion + ", highestVersion="
				+ highestVersion + ", access=" + access + ", query=" + query
				+ ", doc=" + doc + "]";
	}
}
