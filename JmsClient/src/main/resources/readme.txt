The JmsClient is a simple command line app (spring-boot) to send/receive messages from an ActiveMq broker. It can act as a producer and also as a consumer.

See application.properties for parameters and default values

Can add brokerUrl, username, password to java command line:
-Damq.brokerURL=failover:(tcp://localhost:61616)?jms.prefetchPolicy.all=2 -Damq.userName=admin -Damq.password=admin

Examples:

Producer - send one message from a file
java -Dproducer.messageFile=/tmp/hello.txt -Ddestination=testQ -jar JmsClient.jar producer

Producer send many generated messages
java -Dproducer.messageCount=100 -Dproducer.messageSize=1024 -Ddestination=testQ -jar JmsClient.jar producer

Consumer - read from queue
java -Dconsumer.concurrency=2 -Dconsumer.wait=1000 -Ddestination=testQ -jar JmsClient.jar consumer
