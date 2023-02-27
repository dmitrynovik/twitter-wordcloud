## Starting the API client

* Open the file twitterapiclient/src/main/resources/application.properties
* Update the following:
```
twitter.bearer.token=your-bearer-token
Add the following. The rabbitmq value should point your laptop rabbitmq
server.port=8081
message.queue.enabled=true
spring.rabbitmq.host=RABBITMQ_HOST
spring.rabbitmq.password=RABBITMQ_PASSWORD
spring.rabbitmq.port=RABBITMQ_PORT
spring.rabbitmq.username=RABBITMQ_USERNAME
```
* Start twitter client
```
mvnw.cmd install
mvnw.cmd spring-boot:run -pl twitterapiclient
```
This should start the API client

## Starting the MVC
* Open the file modelviewcontroller/src/main/resources/application.properties
* Add the following: 
```
server.port=8082
message.queue.enabled=true
spring.rabbitmq.host=RABBITMQ_HOST
spring.rabbitmq.password=RABBITMQ_PASSWORD
spring.rabbitmq.port=RABBITMQ_PORT
spring.rabbitmq.username=RABBITMQ_USERNAME
```
* Start the mvc client:
```
mvnw.cmd install
mvnw.cmd spring-boot:run -pl modelviewcontroller
```

