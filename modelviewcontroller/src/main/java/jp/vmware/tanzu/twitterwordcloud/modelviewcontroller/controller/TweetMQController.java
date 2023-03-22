package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.controller;

import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.configuration.MvcMQConfiguration;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service.TweetStreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;

import java.io.IOException;

import com.rabbitmq.stream.*;
import java.util.UUID;
// import java.util.concurrent.CountDownLatch;
// import java.util.concurrent.TimeUnit;
// import java.util.concurrent.atomic.AtomicLong;
// import java.util.stream.IntStream;

@Controller
@ConditionalOnProperty(value = "message.queue.enabled", havingValue = "true")
public class TweetMQController {

	private static final Logger logger = LoggerFactory.getLogger(TweetMQController.class);
	private long currentTime, streamStartTime = Long.MAX_VALUE;

	TweetStreamService tweetStreamService;

	public TweetMQController(TweetStreamService tweetStreamService, 
			@Value("${spring.rabbitmq.host}") String rabbitmqHost, 
			@Value("${spring.rabbitmq.username}") String rabbitmqUser,
			@Value("${spring.rabbitmq.password}") String rabbitmqPassword
		) {
		this.tweetStreamService = tweetStreamService;

		logger.info("Connecting to RabbitMQ stream at " + rabbitmqHost + " as " + rabbitmqUser);

		// Streams:
		Address entryPoint = new Address(rabbitmqHost, 5552);
		Environment environment = Environment.builder()
			.host(rabbitmqHost)
			.username(rabbitmqUser)
			.password(rabbitmqPassword)
			.addressResolver(address -> entryPoint)
			.build();  // <1>

        String stream = MvcMQConfiguration.STREAM_NAME;

		//AtomicLong sum = new AtomicLong(0);
        //CountDownLatch consumeLatch = new CountDownLatch(messageCount);
        environment.consumerBuilder()  // <1>
                .stream(stream)
                .offset(OffsetSpecification.first()) // <2>
                .messageHandler((offset, message) -> {  // <3>
                    //sum.addAndGet(Long.parseLong(new String(message.getBodyAsBinary())));  // <4>
                    //consumeLatch.countDown();  // <5>

					long time = offset.timestamp();
					if (time < streamStartTime)
						streamStartTime = time;

					currentTime = time;

					String tweet = new String(message.getBodyAsBinary());
					try {
						tweetHandle(tweet);
					} catch (Exception ex) {
						logger.error(ex.toString());
					}
                })
                .build();
	}

	//@RabbitListener(queues = MvcMQConfiguration.QUEUE_NAME)
	public void tweetHandle(String tweet) throws IOException, InterruptedException {
		logger.debug("Queue Received : " + tweet);
		if (!tweet.isEmpty()) {
			logger.debug("Queue Processing");
			tweetStreamService.handler(tweet);
		}
	}

	@RabbitListener(queues = "#{mvcMQConfiguration.getNotificationQueue()}")
	public void notificationHandle(String tweet) throws IOException {
		logger.debug("Queue Received : " + tweet);
		tweetStreamService.notifyTweetEvent(tweet);
	}

	// Streams start here:


}
