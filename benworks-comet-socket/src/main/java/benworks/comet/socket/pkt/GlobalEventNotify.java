/**
 * 
 */
package benworks.comet.socket.pkt;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;

/**
 * 处理ha.net发过来的全局性事件 ------------------------------------------ GLOBAL_EVENT
 * 0x64 G=>S G->G 全局事件
 * 
 * properties Properties 额外属性 event UINT32 事件类型。 目前支持: 0x1
 * 当一个ip被ha自动禁止(ip_banned) 0x2 当一个host被ha自动禁止(host_banned) value STRING type ==
 * 0x01 为被封的ip type==0x02为被封的host的host_id
 * ------------------------------------------
 * 
 * @author bin.liu
 * 
 */
public class GlobalEventNotify extends Packet implements PacketHandler {

	private final static Log log = LogFactory.getLog(GlobalEventNotify.class);

	@Field(index = 0, name = "处理ha.net发过来的全局性事件", catalog = "generic")
	public final static short PACKET_TYPE = 0x64;
	@Field(index = 1, name = "额外信息")
	private Map<String, String> properties;
	@Field(index = 2, name = "事件")
	private int events;
	@Field(index = 3, name = "值")
	private String value;

	public GlobalEventNotify() {
		super(PACKET_TYPE);
	}

	public GlobalEventNotify(Map<String, String> properties, int events,
			String value) {
		super(PACKET_TYPE);
		this.properties = properties;
		this.events = events;
		this.value = value;
		length += mapLength(properties) + 4 + stringLength(value);
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		putMap(body, properties);
		body.putInt(events);
		putString(body, value);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.properties = readMap(buffer);
		this.events = buffer.getInt();
		this.value = readString(buffer);
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	public int getEvents() {
		return events;
	}

	public String getValue() {
		return value;
	}

	@Override
	public void handle(IoSession session) {
		log.info("global event(" + events + "):" + value);
	}

}
