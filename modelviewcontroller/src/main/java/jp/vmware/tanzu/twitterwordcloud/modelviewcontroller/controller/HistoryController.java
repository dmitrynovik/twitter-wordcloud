package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.controller;

import java.util.Date;
import java.util.List;

import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.NameResolutionException;
import org.apache.geode.cache.query.QueryInvocationTargetException;
import org.apache.geode.cache.query.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.CachedTweet;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;

@Controller
public class HistoryController {
    private final CacheQueryService cacheService;

	public HistoryController(final CacheQueryService cacheService) {
		this.cacheService = cacheService;
    }

    @GetMapping({ "/get-latest" })
    public ResponseEntity<List<CachedTweet>>  getAllTweets(@RequestParam("since") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date since) 
        throws FunctionDomainException, TypeMismatchException, NameResolutionException, QueryInvocationTargetException {
        List<CachedTweet> tweets = cacheService.getAllTweetsSince(since);
        return new ResponseEntity<>(tweets, HttpStatus.OK);
    }
}
