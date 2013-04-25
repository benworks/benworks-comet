package benworks.comet.broadcast;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import benworks.comet.socket.ServiceAcceptorClient;

/**
 * connector的配置
 * 
 * @author benworks
 * 
 */
@Configuration
public class ConnectorConfig {

	@Value("${ha.ip}")
	private String connectorIp;

	@Value("${ha.port}")
	private int connectorPort;

	@Value("${ha.service.id}")
	private int serviceId;

	@Value("${ha.service.name}")
	private String serviceName;

	@Value("${ha.service.doc}")
	private String serviceDoc;

	@Bean
	public ServiceAcceptorClient serviceAcceptorClient() {
		ServiceAcceptorClient instance = ServiceAcceptorClient.getInstance();
		instance.setIp(connectorIp);
		instance.setPort(connectorPort);
		instance.setClientId(serviceId);
		instance.setName(serviceName);
		instance.setDoc(serviceDoc);
		return instance;
	}
}
