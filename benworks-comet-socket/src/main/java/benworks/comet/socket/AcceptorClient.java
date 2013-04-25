package benworks.comet.socket;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import benworks.comet.socket.pkt.Packet;
import benworks.comet.socket.pkt.PingRequest;
import benworks.comet.socket.pkt.SslOpenRequest;

/**
 * 协议包服务的客户端端实现
 * 
 * @author benworks
 * 
 */
public class AcceptorClient {

	private final static Log log = LogFactory.getLog(AcceptorClient.class);
	private final static AtomicInteger PING_SERIAL = new AtomicInteger(0);
	private final static byte[] PING_DATA = new byte[512];

	private NioSocketConnector connector;
	private IoSession session;
	private String ip;
	private int port;
	private int clientId;
	private int gateId;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getClientId() {
		return clientId;
	}

	public void setClientId(int clientId) {
		this.clientId = clientId;
	}

	public int getGateId() {
		return gateId;
	}

	public void setGateId(int gateId) {
		this.gateId = gateId;
	}

	public boolean isConnect() {
		return session != null;
	}

	public void close() {
		try {
			if (session != null)
				session.close(true);
			session = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void destroy() {
		close();
		if (connector != null)
			connector.dispose();
		connector = null;
	}

	/**
	 * 如果已经连接上，此方法不做任何事情，可通过此方法做重连
	 */
	public void connect() {
		try {
			if (connector == null) {
				connector = new NioSocketConnector();
				connector.getFilterChain().addLast(
						"codec",
						new ProtocolCodecFilter(new PacketEncoder(),
								new PacketDecoder()));
				// acceptor.getFilterChain().addLast("logger", new
				// LoggingFilter());
				connector.setHandler(new AcceptorClientPacketIoHandler());
				connector.setConnectTimeoutMillis(10000);
				connector.setConnectTimeoutCheckInterval(10000);
				connector.getSessionConfig().setMinReadBufferSize(512);
				connector.getSessionConfig().setMaxReadBufferSize(16 * 1024);
				connector.getSessionConfig().setSendBufferSize(16 * 1024);
				connector.getSessionConfig().setReceiveBufferSize(16 * 1024);
			}
			if (session == null) {
				ConnectFuture future = connector.connect(new InetSocketAddress(
						ip, port));
				future.addListener(new IoFutureListener<ConnectFuture>() {

					@Override
					public void operationComplete(ConnectFuture future) {
						if (future.getException() != null)
							log.error(future.getException(),
									future.getException());
					}
				});
				log.info("Connect to " + ip + ":" + port);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void send(Packet packet) {
		if (session != null && packet != null) {
			packet.setSrcId(clientId);
			packet.setDestId(gateId);
			session.write(packet);
		}
	}

	public void sendAndWaitFinish(Packet packet) {
		if (session != null && packet != null) {
			packet.setSrcId(clientId);
			packet.setDestId(gateId);
			WriteFuture future = session.write(packet);
			try {
				future.await();
			} catch (Exception e) {
				log.error(e, e);
			}
		}
	}

	/**
	 * 发送1K的数据作为测试数据
	 */
	public void ping() {
		send(new PingRequest(PING_SERIAL.incrementAndGet(),
				System.currentTimeMillis(), PING_DATA));
	}

	public void openSsl() {
		SslOpenRequest openSslRequest = new SslOpenRequest(null,
				new int[] { 0 }, 0, "".getBytes());
		openSslRequest.setSrcId(getClientId());
		openSslRequest.setDestId(0);
		send(openSslRequest);
	}

	private void setSession(IoSession session) {
		this.session = session;
	}

	protected void onSessionClosed(IoSession session) {
		close();
	}

	protected void onSessionOpened(IoSession session) {
		setSession(session);
	}

	protected void onMessageReceived(IoSession session, Object message) {
		setSession(session);
	}

	protected void onMessageSent(IoSession session, Object message) {
		setSession(session);
	}

	class AcceptorClientPacketIoHandler extends PacketIoHandler {

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			onSessionClosed(session);
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			onSessionOpened(session);
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause)
				throws Exception {
			log.error(cause, cause);
		}

		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			super.messageReceived(session, message);
			onMessageReceived(session, message);
		}

		@Override
		public void messageSent(IoSession session, Object message)
				throws Exception {
			super.messageSent(session, message);
			onMessageSent(session, message);
		}
	}
}
