/**
 * 
 */
package benworks.comet.broadcast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import benworks.comet.socket.ConnectorManager;

/**
 * 
 * 
 * @author benworks
 * 
 */
@Service
public class ChannelManager {
	private Map<String, Channel> channelMap = new ConcurrentHashMap<String, Channel>();

	@Autowired
	private ConnectorManager connectorManager;

	/**
	 * 通过频道名字获得Channel，如果没有找到频道，创建一个默认的频道
	 * 
	 * @param name
	 * @return
	 */
	public Channel getChannel(String name) {
		Channel channel = channelMap.get(name);
		if (channel != null)
			return channel;
		channel = new Channel(name, connectorManager);
		registerChannel(channel);
		return channel;
	}

	/**
	 * 注册频道
	 * 
	 * @param channel
	 */
	public void registerChannel(Channel channel) {
		channelMap.put(channel.getName(), channel);
	}

	/**
	 * 移除频道，但并非销毁，销毁频道使用Channel.destroy()
	 * 
	 * @param name
	 */
	public void unregisterChannel(String name) {
		channelMap.remove(name);
		connectorManager.deleteGroup(name);
	}

}
