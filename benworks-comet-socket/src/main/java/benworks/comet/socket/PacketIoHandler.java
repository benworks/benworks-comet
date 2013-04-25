/**
 * 
 */
package benworks.comet.socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

import benworks.comet.socket.pkt.PacketHandler;

/**
 * 协议处理器，本处理器主要处理接收行为
 * 
 * @author benworks
 * 
 */
public class PacketIoHandler extends IoHandlerAdapter {

	private final static Log log = LogFactory.getLog(PacketIoHandler.class);

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		log.error(cause, cause);
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		if (message == null) {
			log.error("Message null");
		}
		if (message instanceof PacketHandler) {
			PacketHandler handler = (PacketHandler) message;
			if (log.isDebugEnabled())
				log.debug("PacketHandler find:" + handler);
			handler.handle(session);
		} else
			log.error("No handler find for " + message.getClass());
	}

	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		if (log.isDebugEnabled())
			log.debug("Message send:" + message);
	}

}
