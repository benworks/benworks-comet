package benworks.comet.socket;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.ArrayUtils;

import benworks.comet.socket.pkt.AddBlackOrWhiteList;
import benworks.comet.socket.pkt.BroadcastMessageReqAndRes;
import benworks.comet.socket.pkt.DeleteBlackOrWhiteList;
import benworks.comet.socket.pkt.DirectMessageReqAndRes;
import benworks.comet.socket.pkt.ElementJoinRequest;
import benworks.comet.socket.pkt.ElementLeaveRequest;
import benworks.comet.socket.pkt.GroupAddRequest;
import benworks.comet.socket.pkt.GroupMessageReqAndRes;
import benworks.comet.socket.pkt.GroupRemoveRequest;
import benworks.comet.socket.pkt.ServicePublishRequest;
import benworks.comet.socket.pkt.ServiceQueryRequest;
import benworks.comet.socket.pkt.StateChangeEventRequest;

/**
 * 发送数据包给gate，避免应用直接使用Packet
 * 
 * @author benworks
 * 
 */
public class ConnectorManager {
	private static ConnectorManager instance = new ConnectorManager();
	public Map<String, Long> groupMapping = new ConcurrentHashMap<String, Long>();
	private AtomicLong groupIdCounter = new AtomicLong(2);

	public static ConnectorManager getInstance() {
		return instance;
	}

	private ConnectorManager() {

	}

	private synchronized long findGroupByName(String name) {
		Long id = groupMapping.get(name);
		if (null != id) {
			return id;
		} else {
			long newId = ServiceAcceptorClient.getInstance().getClientId();
			newId <<= 32;
			newId |= groupIdCounter.getAndIncrement();
			groupMapping.put(name, newId);
			addGroups(newId, name);
			return newId;
		}
	}

	/**
	 * 廣播組管理
	 * 
	 * @param id
	 * @param name
	 */
	private void addGroups(long id, String name) {
		AcceptorClient client = ServiceAcceptorClient.getInstance();
		Map<String, String> properites = Collections.emptyMap();
		GroupAddRequest add = new GroupAddRequest(properites, id, name);
		client.send(add);
	}

	/**
	 * 单播
	 * 
	 * @param sessionId
	 * @param message
	 */
	public void singleBroadcast(int sessionId, byte[] message) {
		AcceptorClient client = ServiceAcceptorClient.getInstance();
		DirectMessageReqAndRes direct = new DirectMessageReqAndRes(sessionId,
				0x01, message);
		client.send(direct);
	}

	/**
	 * 多播
	 * 
	 * @param sessionId
	 * @param message
	 */
	public void multiBroadcast(int[] sessionIds, byte[] message) {
		AcceptorClient client = ServiceAcceptorClient.getInstance();
		BroadcastMessageReqAndRes broadcase = new BroadcastMessageReqAndRes(
				sessionIds, 0x01, message);
		client.send(broadcase);
	}

	/**
	 * 组播
	 * 
	 * @param groupId
	 *            (不应该创建新的广播组)所以此处应传long组ID
	 * @param message
	 */
	public void groupBroadcast(String group, byte[] message) {
		groupBroadcast(group, ArrayUtils.EMPTY_INT_ARRAY, message);
	}

	/**
	 * 组播
	 * 
	 * @param groupId
	 *            (不应该创建新的广播组)所以此处应传long组ID
	 * @param excludeSids
	 *            排除在外的sessionId
	 * @param message
	 */
	public void groupBroadcast(String group, int[] excludeSids, byte[] message) {
		AcceptorClient client = ServiceAcceptorClient.getInstance();
		Long groupId = this.groupMapping.get(group);
		if (null != groupId) {
			GroupMessageReqAndRes groupMessarge = new GroupMessageReqAndRes(
					groupId, excludeSids, message, 0x01);
			client.send(groupMessarge);
		}
	}

	/**
	 * 服务器间广播
	 * 
	 * @param groupId
	 *            (不应该创建新的广播组)所以此处应传long组ID
	 * @param excludeSids
	 *            排除在外的sessionId
	 * @param message
	 */
	public void serviceBroadcast(String serviceGroup, byte[] message) {
		groupBroadcast(serviceGroup, message);
	}

	/**
	 * create new group
	 * 
	 * @param group
	 */
	public void createGroup(String group) {
		findGroupByName(group);
	}

	/**
	 * 删除广播组
	 * 
	 * @param group
	 *            (不应该创建新的广播组)所以此处应传long组ID
	 */
	public void deleteGroup(String group) {
		AcceptorClient client = ServiceAcceptorClient.getInstance();
		GroupRemoveRequest remove = new GroupRemoveRequest(
				Long.parseLong(group));
		Long gid = groupMapping.get(remove.getGroups());
		if (gid != null) {
			client.send(remove);
			groupMapping.remove(remove.getGroups());
		}
	}

	/**
	 * 添加节点到广播组
	 * 
	 * @param group
	 * @param sessionId
	 */
	public void joinGroup(String group, int sessionId) {
		AcceptorClient client = ServiceAcceptorClient.getInstance();
		Map<String, String> map = new HashMap<String, String>();
		ElementJoinRequest elementJoin = new ElementJoinRequest(
				this.findGroupByName(group), map, sessionId,
				String.valueOf(sessionId), 0, 0);
		client.send(elementJoin);
	}

	/**
	 * 离开广播组
	 * 
	 * @param group
	 *            (不应该创建新的广播组)所以此处应传long组ID
	 * @param sessionId
	 */
	public void leaveGroup(String group, int sessionId) {
		AcceptorClient client = ServiceAcceptorClient.getInstance();
		Long gid = this.groupMapping.get(Integer.parseInt(group));
		if (gid == null)
			return;
		ElementLeaveRequest leave = new ElementLeaveRequest(gid, sessionId);
		client.send(leave);
	}

	/**
	 * 强制断开连接
	 * 
	 * @param sessionId
	 */
	public void forceDisconnect(int sessionId) {
		AcceptorClient client = ServiceAcceptorClient.getInstance();
		StateChangeEventRequest reqest = new StateChangeEventRequest(sessionId,
				(short) 0, 0, "强制断开连接");
		client.send(reqest);
	}

	/**
	 * 禁止IP地址访问（封IP）
	 * 
	 * @param ip
	 */
	public void banIp(String ip) {
		AcceptorClient client = ServiceAcceptorClient.getInstance();
		try {
			InetAddress.getByName(ip);
			byte type = 1;
			String[] blackList = new String[1];
			String[] whiteList = new String[1];
			blackList[0] = ip;
			AddBlackOrWhiteList addBlackOrWhiteList = new AddBlackOrWhiteList(
					type, blackList, whiteList);
			client.send(addBlackOrWhiteList);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 允许IP地址访问（解封IP）
	 * 
	 * @param ip
	 */
	public void unbanIp(String ip) {
		AcceptorClient client = ServiceAcceptorClient.getInstance();
		try {
			InetAddress.getByName(ip);
			byte type = 1;
			String[] blackList = new String[1];
			String[] whiteList = new String[1];
			whiteList[0] = ip;
			DeleteBlackOrWhiteList eeleteBlackOrWhiteList = new DeleteBlackOrWhiteList(
					type, blackList, whiteList);
			client.send(eeleteBlackOrWhiteList);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据服务类型，查询相应服务。
	 * 
	 * @param serviceType
	 *            服务类型
	 */
	public void queryService(String serviceType) {
		AcceptorClient client = ServiceAcceptorClient.getInstance();
		Map<String, String> properties = new HashMap<String, String>();
		ServiceQueryRequest serviceQueryRequest = new ServiceQueryRequest(
				properties, serviceType);
		client.send(serviceQueryRequest);
	}

	/**
	 * 发布服务
	 * 
	 * @param serviceType
	 *            服务类型
	 * @param name
	 *            名称
	 * @param doc
	 *            服务描述
	 */
	public void publishService(int sessionId, String serviceType, String name,
			String doc) {
		AcceptorClient client = ServiceAcceptorClient.getInstance();
		// 首先在服务器上注册 一个自定义类型服务，
		ServicePublishRequest servicePublishRequest = new ServicePublishRequest(
				sessionId, serviceType, name, doc);
		client.send(servicePublishRequest);
	}
}
