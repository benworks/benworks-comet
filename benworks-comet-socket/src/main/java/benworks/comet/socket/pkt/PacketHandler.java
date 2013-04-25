package benworks.comet.socket.pkt;

import org.apache.mina.core.session.IoSession;

/**
 * packet处理器接口
 * 
 * @author benworks
 * 
 */
public interface PacketHandler {

	void handle(IoSession session);

}
