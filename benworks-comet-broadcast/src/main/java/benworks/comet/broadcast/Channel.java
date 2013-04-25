/**
 * 
 */
package benworks.comet.broadcast;

import benworks.comet.socket.ConnectorManager;

/**
 * Channel的默认实现
 * 
 * @author benworks
 * 
 */
public class Channel {

	private String name;
	private ConnectorManager connectorManager;

	public Channel(String name, ConnectorManager connectorManager) {
		this.name = name;
		this.connectorManager = connectorManager;
	}

	public String getName() {
		return name;
	}

	public void broadcast(BroadcastMessage message, int... excludeSids) {
		if (excludeSids.length > 0) {
			connectorManager.groupBroadcast(name, excludeSids,
					Amf3Utils.getAmf3ByteArray(message));
		} else {
			connectorManager.groupBroadcast(name,
					Amf3Utils.getAmf3ByteArray(message));
		}
	}

	public String toString() {
		return "Channel:" + name;
	}
}
