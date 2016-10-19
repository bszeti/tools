package com.mycompany.jmsclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Application {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) throws Exception {
    	log.info("Starting app...");
    	ApplicationContext ctx = SpringApplication.run(Application.class, args);
    	
    	MyJmsProducer myJmsProducer = ctx.getBean(MyJmsProducer.class);
    	myJmsProducer.sendMessages();
    }

}
