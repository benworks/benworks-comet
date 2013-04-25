package benworks.comet.socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import benworks.comet.socket.pkt.Packet;

/**
 * 解包处理
 * 
 * @author benworks
 * 
 */
public class PacketDecoder extends CumulativeProtocolDecoder {
	private final static Log log = LogFactory.getLog(PacketDecoder.class);

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		if (!enoughRemaining(in))
			return false;

		out.write(toPacket(in));
		return true;
	}

	/**
	 * 判断是否有足够的剩余字节生成协议包
	 * 
	 * @param in
	 * @param headerLength
	 * @return
	 */
	private boolean enoughRemaining(IoBuffer in) {
		int originPos = in.position();
		int length = in.getInt();
		in.position(originPos);
		boolean enoughRemain = true;
		if (in.remaining() < length - Packet.PACKET_HEADER_LENGTH) {
			enoughRemain = false;
		}
		return enoughRemain;
	}

	/**
	 * 把字节转换成后续程序用的packet实例
	 * 
	 * @param in
	 * @return
	 */
	private Packet toPacket(IoBuffer in) {
		int originPos = in.position();
		// length(4)+srcId(4)+destId(4)
		in.position(originPos + 12);
		// packetType
		short packetType = in.getShort();
		in.position(originPos);
		Packet packet = PacketMap.getPacket(packetType, in);
		if (packet == null) {
			log.warn("No match packet for type:" + packetType);
			return packet;
		}
		if (log.isDebugEnabled())
			log.debug("Packet find:" + packet.getClass().getName());
		return packet;
	}
}