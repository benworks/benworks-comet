package benworks.comet.broadcast;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import benworks.comet.socket.ConnectorCallback;
import benworks.comet.socket.pkt.ServiceInfo;

@Service
public class ConnectorCallbackImpl implements ConnectorCallback {

	@Autowired
	private ServiceNodeManager serviceNodeManager;

	@Autowired
	private RequestProcessor requestProcessor;

	@Override
	public void disconnect(int sid) {

	}

	@Override
	public void receive(byte[] message) {
		Object obj = Amf3Utils.getObjectFromAmf3ByteArray(message);
		if (obj == null)
			return;
		if (obj instanceof GeneralResponse)
			ServiceNode.putResponse((GeneralResponse) obj);
		if (obj instanceof GeneralRequest)
			requestProcessor.process((GeneralRequest) obj);
	}

	@Override
	public void serviceType(ServiceInfo[] services) {
		serviceNodeManager.refresh(services);
	}

}
