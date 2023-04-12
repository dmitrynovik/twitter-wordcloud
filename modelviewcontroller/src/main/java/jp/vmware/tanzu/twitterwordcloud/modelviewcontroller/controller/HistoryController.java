package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.CachedTweet;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;

@Controller
public class HistoryController {
    private final CacheQueryService cacheService;
    private static final Logger logger = LoggerFactory.getLogger(HistoryService.class);

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
