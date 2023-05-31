## Starting the API client
* Define the environment variable TWITTER_API_BEARER_TOKEN = (your Twitter API v2 bearer token. You need to regitster as Twitter developer to run this.)
* Open the file twitterapiclient/src/main/resources/application.properties
* Update the following:
```
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
./mvnw install
./mvnw spring-boot:run -pl twitterapiclient
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
./mvnw install
./mvnw spring-boot:run -pl modelviewcontroller
```

* Add to activate postgres
```
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.password=POSTGRES_PW
spring.datasource.url=jdbc:postgresql://{host}:{port}/{database}
spring.datasource.username=POSTGRES_USER
```

* Add to activate redis
```
spring.session.store-type=redis
spring.redis.cluster.nodes=REDIS_NODES  

```
