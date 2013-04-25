/**
 * 
 */
package benworks.comet.socket.pkt;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * Service向Gates注销自己：广播SERVICE_UNPUBLISH给所有Gates .
 * 当Service连接断开后，Gate会自动删除该服务信息（并且广播其他Gates） SERVICE_UNPUBLISH结构:
 * ------------------------------------------------------ 字段 类型 描述 id ServiceID
 * 要注销的服务的ID(必须是自己) SERVICE_UNPUBLISH 0x31 S=>GS,G=>GS 1. Service主动从名字服务注销自己 2.
 * Service离开时，gate自动发广播要求所有gate删除该service信息
 * ------------------------------------------------------
 * 
 * @author binliu
 * 
 */
public class ServiceUnpublishRequest extends Packet {
	@Field(index = 0, name = "版本协商", catalog = "generic")
	public final static short PACKET_TYPE = 0x31;
	@Field(index = 1, name = "要注销的服务的ID(必须是自己)")
	private int serviceId;

	public ServiceUnpublishRequest() {
		super(PACKET_TYPE);
	}

	public ServiceUnpublishRequest(int serviceId) {
		super(PACKET_TYPE);
		this.serviceId = serviceId;
		length += 4;
	}

	protected void toBuffer(IoBuffer body) {
		super.toBuffer(body);
		body.putInt(serviceId);
	}

	public void fromBuffer(IoBuffer buffer) {
		super.fromBuffer(buffer);
		this.serviceId = buffer.getInt();
	}

	public int getServiceId() {
		return serviceId;
	}

}
