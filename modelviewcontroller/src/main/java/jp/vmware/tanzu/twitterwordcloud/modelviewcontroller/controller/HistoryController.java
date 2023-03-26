package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.controller;

import java.util.List;

import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.NameResolutionException;
import org.apache.geode.cache.query.QueryInvocationTargetException;
import org.apache.geode.cache.query.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.CachedTweet;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service.*;
import org.springframework.http.*;

@Controller
public class HistoryController {
    private final CacheQueryService cacheService;

	public HistoryController(final CacheQueryService cacheService) {
		this.cacheService = cacheService;
        //historyService.readFromRabbitMQStream();
    }

    @GetMapping({ "/all-history" })
    public ResponseEntity<List<CachedTweet>>  getAllTweets() throws FunctionDomainException, TypeMismatchException, NameResolutionException, QueryInvocationTargetException {
        List<CachedTweet> tweets = cacheService.getAllTweets();
        return new ResponseEntity<>(tweets, HttpStatus.OK);
    }
}
