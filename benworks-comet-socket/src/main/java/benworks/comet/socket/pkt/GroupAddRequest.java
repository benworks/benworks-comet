package benworks.comet.socket.pkt;

import java.util.Map;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * 添加廣播組 0x41
 * 
 * @author hebe.liu
 * 
 */
public class GroupAddRequest extends Packet {
	@Field(index = 0, name = "添加廣播組", catalog = "generic")
	public final static short PACKET_TYPE = 0x41;

	@Field(index = 1, name = "组的容量:默认1;实为旧协议 GroupInfo[] groups.length,而旧协议中此Length永远只为1;此字段目的为占位")
	private int groupLength = 1;

	@Field(index = 2, name = "額外屬性")
	private Map<String, String> properties;

	@Field(index = 3, name = "廣播組ID")
	private long id;

	@Field(index = 4, name = "組名")
	private String name;

	@Field(index = 5, name = "消息过滤设置。通过bit掩码方式过滤,已经定义的类型(0-7 bits系统保留，不允许app定义 "
			+ "lossable  = 0x01  --是否允许不可靠消息通过  8-31bit允许app自定义")
	private int filter = 0xffff;

	public GroupAddRequest() {
		super(PACKET_TYPE);
	}

	public GroupAddRequest(Map<String, String> properties, long id, String name) {
		super(PACKET_TYPE);
		this.properties = properties;
		this.id = id;
		this.name = name;
		this.length += mapLength(properties) + 8 + stringLength(name) + 8;
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putInt(groupLength);
		putMap(body, properties);
		body.putLong(this.getId());
		putString(body, name);
		body.putInt(filter);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.groupLength = buffer.getInt();
		this.properties = readMap(buffer);
		this.id = buffer.getInt();
		this.name = readString(buffer);
		this.filter = buffer.getInt();
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getFilter() {
		return filter;
	}

	public int getGroupLength() {
		return groupLength;
	}
}
