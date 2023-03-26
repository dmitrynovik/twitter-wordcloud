package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service;

import java.util.List;

import org.apache.geode.cache.*;
import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.NameResolutionException;
import org.apache.geode.cache.query.QueryInvocationTargetException;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.SelectResults;
import org.apache.geode.cache.query.TypeMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.CachedTweet;

@Service
public class CacheService {
    private static final Logger logger = LoggerFactory.getLogger(HistoryService.class);
	private final QueryService queryService;
    
	public CacheService(GemFireCache cache) {
		this.queryService = cache.getQueryService();
	}

    @Cacheable(cacheNames = "Tweets", key = "#id")
	public CachedTweet getTweet(String id) {
		return null;
	}

	@CachePut(cacheNames = "Tweets", key = "#result.id")
	public CachedTweet cache(CachedTweet tweet) {
		logger.debug("Caching tweet: " + tweet.id);
		return tweet;
	}

	public List<CachedTweet> executeQuery(String query) throws FunctionDomainException, TypeMismatchException, NameResolutionException, QueryInvocationTargetException {
		SelectResults results = (SelectResults) queryService.newQuery(query).execute();
		return results.asList();
	}
}
