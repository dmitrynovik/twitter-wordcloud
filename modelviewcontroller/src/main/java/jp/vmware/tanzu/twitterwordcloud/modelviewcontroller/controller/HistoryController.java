package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    }

    @GetMapping({ "/get-latest" })
    public ResponseEntity<List<CachedTweet>>  getAllTweets(@RequestParam(name = "since", required = false) String since) throws Exception {
        Date sinceDate;
        try {
            sinceDate =  new SimpleDateFormat("yyyy-MM-dd").parse(since);
        } catch (Exception e) {
            sinceDate = new Date(0);
        }
       
        List<CachedTweet> tweets = cacheService.getAllTweetsSince(sinceDate);
        return new ResponseEntity<>(tweets, HttpStatus.OK);
    }
}
