package benworks.comet.broadcast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import benworks.comet.common.timer.ScheduleManager;
import benworks.comet.socket.ConnectorManager;
import benworks.comet.socket.pkt.ServiceInfo;

/**
 * 
 * 
 * @author benworks
 * 
 */
@Service
public class ServiceNodeManager {
	private static final Log log = LogFactory.getLog(ServiceNodeManager.class);

	public static int NODE_REFRESH_PERIOD_MINUTE = 2;

	public static String SERVICE_TYPE = "com/xlands/ihome2app/V1";

	@Autowired
	private ConnectorManager connectorManager;
	@Autowired
	private ScheduleManager scheduleManager;

	private Map<Integer, ServiceNode> serviceNodeMap = new ConcurrentHashMap<Integer, ServiceNode>();

	private String serviceType;
	private int refreshPeriodMinute;

	public ServiceNodeManager() {
		this(SERVICE_TYPE, NODE_REFRESH_PERIOD_MINUTE);
	}

	public ServiceNodeManager(String serviceType, int refreshPeriodMinute) {
		this.serviceType = serviceType;
		this.refreshPeriodMinute = refreshPeriodMinute;
	}

	@PostConstruct
	public void init() {
		scheduleManager.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					connectorManager.queryService(serviceType);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}, 1 * DateUtils.MILLIS_PER_MINUTE, refreshPeriodMinute
				* DateUtils.MILLIS_PER_MINUTE);
	}

	/**
	 * 通过serverIndex获得服务节点
	 * 
	 * @param name
	 * @return
	 */
	public ServiceNode getServiceNode(int serverIndex) {
		return serviceNodeMap.get(serverIndex);
	}

	public boolean rpc(int serverIndex, String action, Object... params) {
		ServiceNode node = serviceNodeMap.get(serverIndex);
		GeneralRequest request = new GeneralRequest();
		request.setAction(action);
		request.setParams(params);
		request.setSid(node.getSid());
		request.setSerial(serverIndex);
		return node.rpc(request);
	}

	public GeneralResponse rpcWithResponse(int serverIndex, String action,
			Object... params) {
		ServiceNode node = serviceNodeMap.get(serverIndex);
		GeneralRequest request = new GeneralRequest();
		request.setAction(action);
		request.setParams(params);
		request.setSid(node.getSid());
		request.setSerial(serverIndex);
		return node.rpcWithResponse(request);
	}

	/**
	 * 从Connector那里刷新最新的服务节点
	 * 
	 * @param channel
	 */
	public void refresh(ServiceInfo[] services) {
		serviceNodeMap.clear();
		for (ServiceInfo info : services) {
			String name = info.getName();
			if (StringUtils.isEmpty(name))
				continue;
			String[] serverIndexAry = name.split(",");
			for (String serverIndexString : serverIndexAry) {
				try {
					int serverIndex = Integer.parseInt(serverIndexString);
					ServiceNode node = new ServiceNode(info.getId(),
							serverIndex, info.getDoc(), connectorManager);
					serviceNodeMap.put(serverIndex, node);
					log.info("### :" + info.getDoc() + ",serverIndex:"
							+ serverIndex + " refresh");
				} catch (Exception e) {
					log.warn("### serverName:" + info.getDoc()
							+ ",serverIndexs:" + info.getName()
							+ " can not refresh," + e);
					e.printStackTrace();
				}
			}
		}
	}

}
