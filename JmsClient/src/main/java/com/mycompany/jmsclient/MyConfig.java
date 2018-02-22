package com.mycompany.jmsclient;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.StringUtils;

@Configuration
public class MyConfig {
	private static final Logger log = LoggerFactory.getLogger(MyConfig.class);

	@Value("${amq.userName}")
	String userName;

	@Value("${amq.password}")
	String password;

	@Value("${amq.brokerURL}")
	String brokerURL;

	@Value("${useTopic}")
	boolean useTopic;

	@Value("${destination}")
	String destination;
	
	@Value("${clientId:#{null}}")
	String clientId;

	@Value("${amq.connPoolSize}")
	int connPoolSize;

	@Bean
	public ConnectionFactory connFactory() {
		ActiveMQConnectionFactory connFactory = new ActiveMQConnectionFactory(userName, password, brokerURL);
		//Required for useAsyncSend
		connFactory.setProducerWindowSize(10240000);
		if (connPoolSize > 0) {
			PooledConnectionFactory pooledConnectionFactory = new PooledConnectionFactory(connFactory);
			pooledConnectionFactory.setMaxConnections(connPoolSize);			
			//This will stop the thread managing the pool after a few seconds so the app will stop in producer mode
			//This also causes message loss with asyncSend if producer flow control is on for long. 
			pooledConnectionFactory.setIdleTimeout(2000);
			pooledConnectionFactory.setTimeBetweenExpirationCheckMillis(1000);			
			log.info("Pool size: {}", connPoolSize);
			return pooledConnectionFactory;
		}
		return connFactory;
	}

	@Bean
	public JmsTemplate jmsTemplate() throws Exception {
		JmsTemplate jmsTemplate = new JmsTemplate(connFactory());
		jmsTemplate.setPubSubDomain(useTopic);
		jmsTemplate.setDefaultDestinationName(destination);
		log.info("Destination: {}", destination);
		return jmsTemplate;
	}

	@Bean
	JmsListenerContainerFactory<?> jmsContainerFactory() throws Exception {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connFactory());
		factory.setPubSubDomain(useTopic);
		if (!StringUtils.isEmpty(clientId)) factory.setClientId(clientId);
		return factory;
	}

	@Bean
	@Autowired
	MyJmsListener createMyJmsListener(ApplicationArguments args) {
		if (args.getNonOptionArgs().contains("consumer")) {
			return new MyJmsListener();
		}
		return null;
	}


}
