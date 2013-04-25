package benworks.comet.broadcast;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import benworks.comet.socket.ConnectorManager;

/**
 * 
 * @author benworks
 * 
 */
public class ServiceNode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4936096338149848916L;
	private static final Log log = LogFactory.getLog(ServiceNode.class);

	public static long RECEIVE_TIMEOUT = 1000l;// 1秒

	private int sid;
	private int serverIndex;
	private String serverName;
	private ConnectorManager connectorManager;

	private static Map<Integer, ArrayBlockingQueue<GeneralResponse>> responses = new ConcurrentHashMap<Integer, ArrayBlockingQueue<GeneralResponse>>();

	public ServiceNode(int sid, int serverIndex, String serverName,
			ConnectorManager deliverManager) {
		this.sid = sid;
		this.serverIndex = serverIndex;
		this.serverName = serverName;
		this.connectorManager = deliverManager;
	}

	public int getSid() {
		return sid;
	}

	public int getServerIndex() {
		return serverIndex;
	}

	public String getServerName() {
		return serverName;
	}

	public boolean rpc(GeneralRequest request) {
		Object result = call(request);
		if (result instanceof ErrorResponse)
			return false;
		return true;
	}

	public GeneralResponse rpcWithResponse(GeneralRequest request) {
		return call(request);
	}

	public static void putResponse(GeneralResponse response) {
		if (!responses.containsKey(response.getSerial())) {
			log.warn("### Give up the response,request id is:"
					+ response.getSerial() + ",maybe because timeout!");
			return;
		}
		try {
			ArrayBlockingQueue<GeneralResponse> queue = responses.get(response
					.getSerial());
			if (queue != null) {
				queue.put(response);
			} else {
				log.warn("### Give up the response,request id is:"
						+ response.getSerial() + ",because queue is null");
			}
		} catch (Exception e) {
			log.error(
					"### Put response error,request id is:"
							+ response.getSerial(), e);
		}
	}

	private GeneralResponse call(GeneralRequest request) {
		long beginTime = System.currentTimeMillis();
		ArrayBlockingQueue<GeneralResponse> responseQueue = new ArrayBlockingQueue<GeneralResponse>(
				1);
		responses.put(request.getSerial(), responseQueue);
		try {
			connectorManager.singleBroadcast(sid,
					Amf3Utils.getAmf3ByteArray(request));
		} catch (Exception e) {
			responses.remove(request.getSerial());
			responseQueue = null;
			log.error("### Send request error", e);
			throw new RuntimeException(e);
		}
		GeneralResponse result = null;
		try {
			result = responseQueue.poll(
					RECEIVE_TIMEOUT - (System.currentTimeMillis() - beginTime),
					TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			responses.remove(request.getSerial());
			log.error("###  Receive response error", e);
			throw new RuntimeException(e);
		}
		responses.remove(request.getSerial());

		if (result == null) {
			String errorMsg = "### Receive response timeout(" + RECEIVE_TIMEOUT
					+ " ms),server is: " + serverIndex + ",request id is:"
					+ request.getSerial();
			throw new RuntimeException(errorMsg);
		}

		return result;
	}
}
