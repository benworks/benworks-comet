package benworks.comet.socket;

import benworks.comet.socket.pkt.ServiceInfo;

/**
 * connector发过来的数据包解码后会分别调用本接口对应的方法，避免应用直接使用Packet<br>
 * 应用层应该提供本接口的实现。
 * 
 * @author benworks
 * 
 */
public interface ConnectorCallback {

	public void disconnect(int sid);

	public void receive(byte[] message);

	public void serviceType(ServiceInfo[] services);
}
