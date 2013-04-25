package benworks.comet.socket;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import benworks.comet.socket.pkt.Packet;

/**
 * 封包处理
 * 
 * @author benworks
 * 
 */
public class PacketEncoder extends ProtocolEncoderAdapter {

	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		// packet情况
		if (message instanceof Packet) {
			out.write(((Packet) message).toBuffer());
		}
	}
}