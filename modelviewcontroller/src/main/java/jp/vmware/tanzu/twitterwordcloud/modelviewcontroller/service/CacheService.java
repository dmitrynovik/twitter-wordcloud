package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.CachedTweet;

@Service
public class CacheService {
    private static final Logger logger = LoggerFactory.getLogger(HistoryService.class);
    
    @Cacheable(cacheNames = "Tweets", key = "#id")
	public CachedTweet getTweet(String id) {
		return null;
	}

	@CachePut(cacheNames = "Tweets", key = "#result.id")
	public CachedTweet cache(CachedTweet tweet) {
		logger.debug("Caching tweet: " + tweet.id);
		return tweet;
	}
}
