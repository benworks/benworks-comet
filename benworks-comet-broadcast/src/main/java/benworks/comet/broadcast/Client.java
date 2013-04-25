package benworks.comet.broadcast;

import java.io.Serializable;

import benworks.comet.socket.ConnectorManager;

/**
 * 用户个体
 * 
 * @author benworks
 * 
 */
public abstract class Client implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 919975553503324781L;

	private int sid;
	private ConnectorManager connectorManager;

	public Client(int sid, ConnectorManager connectorManager) {
		this.sid = sid;
		this.connectorManager = connectorManager;
	}

	/**
	 * 此会话的唯一标识
	 * 
	 * @return
	 */
	public int getSessionId() {
		return sid;
	}

	/**
	 * 发送广播信息
	 * 
	 * @param message
	 */
	public void deliver(BroadcastMessage message) {
		connectorManager.singleBroadcast(sid,
				Amf3Utils.getAmf3ByteArray(message));
	}

	/**
	 * 发送响应信息
	 * 
	 * @param message
	 */
	public void response(ResponseMessage message) {
		message.setSerial(SerialUtils.get());
		connectorManager.singleBroadcast(sid,
				Amf3Utils.getAmf3ByteArray(message));
	}

	/**
	 * 加入频道
	 * 
	 * @param channelName
	 */
	public void joinChannel(String channelName) {
		connectorManager.joinGroup(channelName, sid);
	}

	/**
	 * 离开频道
	 * 
	 * @param channelName
	 */
	public void leaveChannel(String channelName) {
		connectorManager.leaveGroup(channelName, sid);
	}

	public void login() {

	}

	public void logout() {

	}
}
