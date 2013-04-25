/**
 * 
 */
package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Service需要监控/取消所有Gate触发的一些全局事件，需要发送MONITOR_SET给所有Gates，
 * 当关注的事件触发后，Gate会产生GLOBAL_EVENT到关注该事件的所有Services
 * 
 * MONITOR_SET 0x63 S=>GS ,G->G 添加/删除／设置全局监控
 * -------------------------------------------------- MONITOR_SET结构： 字段 类型 描述
 * operation UINT8 操作类型： 0 add 1 remove 2 set
 * 
 * events UINT32 监控的事件类型集合(最多扩展32个事件应该足够) 目前支持: 0x1 当一个ip被ha自动禁止(ip_banned) 0x2
 * 当一个host被ha自动禁止(host_banned)
 * --------------------------------------------------
 * 
 * @author bin.liu
 * 
 */
public class MonitorSet extends Packet {
	@Field(index = 0, name = "监控/取消所有Gate触发的一些全局事件", catalog = "generic")
	public final static short PACKET_TYPE = 0x63;
	@Field(index = 1, name = "操作类型")
	private int operation;
	@Field(index = 2, name = "监控的事件类型集合(最多扩展32个事件应该足够)")
	private int events;

	public MonitorSet() {
		super(PACKET_TYPE);
	}

	public MonitorSet(int operation, int events) {
		super(PACKET_TYPE);
		this.operation = operation;
		this.events = events;
		this.length += 8;
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putInt(operation);
		body.putInt(events);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.operation = buffer.getInt();
		this.events = buffer.getInt();
	}

	public int getOperation() {
		return operation;
	}

	public int getEvents() {
		return events;
	}

}
