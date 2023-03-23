package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.controller;

import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.configuration.MvcMQConfiguration;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.MyTweet;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service.TweetStreamService;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.utils.TweetUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;

import com.rabbitmq.stream.*;
import com.twitter.clientlib.model.*;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

// import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
// import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
// import org.springframework.data.gemfire.config.annotation.EnableCachingDefinedRegions;

// @Controller
// @ConditionalOnProperty(value = "message.queue.enabled", havingValue = "true")
// @ClientCacheApplication(name = "CachingGemFireApplication")
// //@EnableCachingDefinedRegions(clientRegionShortcut = ClientRegionShortcut.LOCAL)
// @EnableGemfireCaching
@SuppressWarnings("unused")
public class HistoryController {

	private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);
	private long currentTime, streamStartTime = Long.MAX_VALUE, offset;

	TweetStreamService tweetStreamService;

	public HistoryController(TweetStreamService tweetStreamService, 
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
			.build();


			environment.consumerBuilder()
				.stream(MvcMQConfiguration.STREAM_NAME)
				.offset(OffsetSpecification.first())
				.messageHandler((offset, message) -> {  

					long time = offset.timestamp();
					if (time < streamStartTime)
						streamStartTime = time;

					currentTime = time;

					try {
						this.offset = offset.offset();
						currentTime = offset.timestamp();
						if (currentTime < streamStartTime)
							streamStartTime = currentTime;

						logger.debug("Start date: " + new Date(this.streamStartTime) + ", End Date: " + new Date(this.currentTime));

						String line = new String(message.getBodyAsBinary());
						StreamingTweetResponse streamingTweetResponse = TweetUtils.setStreamTweetResponse(line);

						if (streamingTweetResponse == null) {
							//Thread.sleep(100);
							return;
						}

						Tweet tweet = streamingTweetResponse.getData();
						MyTweet myTweet = TweetUtils.getMyTweet(streamingTweetResponse, tweet);
						store(myTweet);

					} catch (Exception ex) {
						logger.error(ex.toString());
					}
				})
			.build(); 
		// StreamStats stats = environment.queryStreamStats(MvcMQConfiguration.STREAM_NAME);
		// try {
		// 	offset = stats.committedChunkId();

		// } catch (Exception ex) {
		// 	offset = -1;
		// }
		
		// logger.info("Last stream offset: " + offset);

		//consumer.store(currentTime);
	}

	@CachePut(cacheNames = "Tweets", key = "#tweet.tweetId")
	private void store(MyTweet tweet) {
		
	}
}
