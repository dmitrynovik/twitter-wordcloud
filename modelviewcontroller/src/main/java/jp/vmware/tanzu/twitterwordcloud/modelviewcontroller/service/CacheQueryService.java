package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service;

import java.util.List;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.query.*;
import org.springframework.stereotype.Service;

import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.CachedTweet;

@Service
public class CacheQueryService {
    private final QueryService queryService;

	public CacheQueryService(GemFireCache cache) {
		this.queryService = cache.getQueryService();
	}

	public List<CachedTweet> getAllTweets() throws FunctionDomainException, TypeMismatchException, NameResolutionException, QueryInvocationTargetException {

		SelectResults results = (SelectResults) queryService
			.newQuery("Select * FROM /Tweets")
			.execute();

		// int size = results.size();
		// CachedTweet[] tweets = new CachedTweet[size];

		return results.asList();
	}
}
