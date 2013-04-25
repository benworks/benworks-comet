package benworks.comet.socket;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.session.IoSession;

import benworks.comet.socket.pkt.JoinRequest;

/**
 * service节点跟gate的连接管理
 * 
 * @author benworks
 * 
 */
public class ServiceAcceptorClient extends AcceptorClient {

	private final static Log log = LogFactory
			.getLog(ServiceAcceptorClient.class);
	private static ServiceAcceptorClient instance = new ServiceAcceptorClient();

	private ScheduledExecutorService scheduledExecutorService = Executors
			.newSingleThreadScheduledExecutor();
	private ConnectorCallback connectorCallback;
	private ScheduledFuture<?> reconnectFuture;

	private String name;

	private String doc;

	public static ServiceAcceptorClient getInstance() {
		return instance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDoc() {
		return doc;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}

	public ConnectorCallback getConnectorCallback() {
		return connectorCallback;
	}

	public void setConnectorCallback(ConnectorCallback connectorCallback) {
		this.connectorCallback = connectorCallback;
	}

	private void reconnect() {
		reconnectFuture = scheduledExecutorService.scheduleAtFixedRate(
				new Runnable() {

					@Override
					public void run() {
						try {
							log.info("Service acceptor client reconnecting");
							connect();
						} catch (Throwable e) {
							e.printStackTrace();
						}

					}
				}, DateUtils.MILLIS_PER_MINUTE, DateUtils.MILLIS_PER_MINUTE,
				TimeUnit.MILLISECONDS);
	}

	@Override
	protected void onSessionClosed(IoSession session) {
		super.onSessionClosed(session);
		reconnect();
	}

	@Override
	protected void onSessionOpened(IoSession session) {
		if (reconnectFuture != null && !reconnectFuture.isCancelled())
			reconnectFuture.cancel(true);
		super.onSessionOpened(session);
		log.info("Open ssl first");
		openSsl();
		log.info("Join gate network");
		Map<String, String> map = Collections.emptyMap();
		send(new JoinRequest(map, getClientId(), name,
				JoinRequest.getAllNoLoopbackAddresses(), ""));
	}

}
