package com.mycompany.jmsclient;

import java.io.FileInputStream;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

@Component
public class MyJmsProducer {
	private static final Logger log = LoggerFactory.getLogger(MyJmsProducer.class);
	private static final char[] padding = "0123456789".toCharArray();

	@Autowired
	JmsTemplate jmsTemplate;

	@Autowired
	ApplicationArguments args;

	@Value("${producer.messageCount}")
	int messageCount;

	@Value("${producer.messageSize}")
	int messageSize;

	@Value("${producer.wait}")
	int wait;

	@Value("${producer.messageText:}")
	String messageText;
	
	@Value("${producer.messageFile:}")	
	String messageFile;

	public void sendMessages() throws Exception {

		if (args.getNonOptionArgs().contains("producer")) {

			if (!StringUtils.isEmpty(messageFile)) {
				messageText = StreamUtils.copyToString(new FileInputStream(messageFile), Charset.forName("UTF-8"));
			}
			
			if (StringUtils.isEmpty(messageText)) {
				char[] chars = new char[messageSize];
				for (int i = 0; i < messageSize; i++) {
					chars[i] = padding[i % 10];
				}
				messageText = new String(chars);
			}

			log.info("Sending messages... {}x{}", messageCount, messageText.length());
			for (int i = 0; i < messageCount; i++) {
				jmsTemplate.convertAndSend(messageText);
				log.info("Sent message #{}", i);
				if (wait > 0)
					Thread.sleep(wait);
			}
		}
	}

}
