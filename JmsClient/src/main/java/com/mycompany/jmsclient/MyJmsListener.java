package com.mycompany.jmsclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;

public class MyJmsListener {
	private static final Logger log = LoggerFactory.getLogger(MyJmsListener.class);
	
	@Value("${consumer.wait}")
	int waitTime;
	
	int counter;

	@JmsListener(destination = "${destination}", concurrency="${consumer.concurrency}",containerFactory="jmsContainerFactory")
	public void receiveMessage(String message) throws Exception{
		log.info("Received #{}; [{}]",++counter, message.length());
		Thread.sleep(waitTime);

	}

}
