package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service;

import java.util.Date;
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

	public List<CachedTweet> getAllTweetsSince(Date since) throws FunctionDomainException, TypeMismatchException, NameResolutionException, QueryInvocationTargetException {
		SelectResults results = (SelectResults) queryService
			.newQuery("Select * FROM /Tweets WHERE created > " + since.getTime() / 1000)
			.execute();

		return results.asList();
	}
}
