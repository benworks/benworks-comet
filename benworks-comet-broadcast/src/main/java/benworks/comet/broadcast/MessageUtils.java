package benworks.comet.broadcast;

import java.util.ArrayList;
import java.util.List;

/**
 * 带事务特性的广播工具类
 * 
 * @author benworks
 * 
 */
public class MessageUtils {

	private static ThreadLocal<List<SelfSender>> messages = new ThreadLocal<List<SelfSender>>();

	public static void addMessage(Client client, BroadcastMessage message) {
		List<SelfSender> list = messages.get();
		if (list == null) {
			list = new ArrayList<SelfSender>();
			messages.set(list);
		}
		list.add(new SelfSender(client, message));
	}

	public static void addMessage(Channel channel, BroadcastMessage message) {
		List<SelfSender> list = messages.get();
		if (list == null) {
			list = new ArrayList<SelfSender>();
			messages.set(list);
		}
		list.add(new SelfSender(channel, message));
	}

	public static void commit() {
		List<SelfSender> list = messages.get();
		if (list != null) {
			for (SelfSender sender : list) {
				sender.send();
			}
		}
		clear();
	}

	public static void rollback() {
		clear();
	}

	public static void clear() {
		List<SelfSender> list = messages.get();
		if (list != null)
			list.clear();
		messages.set(null);
	}

	static class SelfSender {
		private Client client;
		private Channel channel;
		private BroadcastMessage message;

		SelfSender(Client client, BroadcastMessage message) {
			this.client = client;
			this.message = message;
		}

		SelfSender(Channel channel, BroadcastMessage message) {
			this.channel = channel;
			this.message = message;
		}

		void send() {
			if (client != null)
				client.deliver(message);
			if (channel != null)
				channel.broadcast(message);
		}
	}

}
