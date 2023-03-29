package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service;

import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.configuration.MvcMQConfiguration;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.CachedTweet;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.MyTweet;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.utils.TweetUtils;

import java.time.ZoneOffset;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.rabbitmq.stream.*;
import com.twitter.clientlib.model.*;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

@Service
@ConditionalOnProperty(value = "message.queue.enabled", havingValue = "true")
public class HistoryService {

	private static final Logger logger = LoggerFactory.getLogger(HistoryService.class);
	private long currentTime, streamStartTime = Long.MAX_VALUE, offset;

	TweetStreamService tweetStreamService;
	private final String rabbitmqHost;
	private final String rabbitmqUser;
	private final String rabbitmqPassword;
	private final CacheService cacheService;
	private long historyOffset;

	public HistoryService(TweetStreamService tweetStreamService,
			CacheService cacheService,
			@Value("${history.offset:0}") long historyOffset, 
			@Value("${spring.rabbitmq.host}") String rabbitmqHost,
			@Value("${spring.rabbitmq.username}") String rabbitmqUser,
			@Value("${spring.rabbitmq.password}") String rabbitmqPassword) {

		this.tweetStreamService = tweetStreamService;
		this.cacheService = cacheService;
		this.historyOffset = historyOffset;
		this.rabbitmqHost = rabbitmqHost;
		this.rabbitmqUser = rabbitmqUser;
		this.rabbitmqPassword = rabbitmqPassword;
	}

	public void readFromRabbitMQStream() {

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
				.offset(OffsetSpecification.offset(historyOffset))
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

						// logger.debug("Start date: " + new Date(this.streamStartTime) + ", End Date: "
						// + new Date(this.currentTime));

						String line = new String(message.getBodyAsBinary());
						StreamingTweetResponse streamingTweetResponse = TweetUtils.setStreamTweetResponse(line);

						if (streamingTweetResponse == null) {
							// Thread.sleep(100);
							return;
						}

						Tweet tweet = streamingTweetResponse.getData();
						MyTweet myTweet = TweetUtils.getMyTweet(streamingTweetResponse, tweet);
						CachedTweet t = new CachedTweet();
						t.text = myTweet.text;
						t.id = myTweet.tweetId;
						t.username = myTweet.username;
						t.created = (int) new Date(currentTime / 1000).getTime();

						CachedTweet cached = cacheService.cache(t);
						if (cacheService.getTweet(cached.id) == null)
							logger.warn("Cache failed");

					} catch (Exception ex) {
						logger.error(ex.toString());
					}
				})
				.build();

		// StreamStats stats =
		// environment.queryStreamStats(MvcMQConfiguration.STREAM_NAME);
		// try {
		// offset = stats.committedChunkId();

		// } catch (Exception ex) {
		// offset = -1;
		// }

		// logger.info("Last stream offset: " + offset);

		// consumer.store(currentTime);
	}
}
