package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.controller;

import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.MyTweet;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service.MyTweetService;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service.HistoryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MyTweetController {

	public final MyTweetService myTweetService;
	public final HistoryService historyService;

	public MyTweetController(MyTweetService myTweetService, HistoryService historyService) {
		this.myTweetService = myTweetService;
		this.historyService = historyService;
	}

	@GetMapping({ "/tweets" })
	public ModelAndView getAllTweets() {
		ModelAndView mav = new ModelAndView("tweets");
		mav.addObject("tweets", myTweetService.findAllByOrderByTweetIdDesc());
		return mav;
	}

	@PostMapping("/tweetDelete")
	public ModelAndView deleteTweet(@ModelAttribute(value = "tweetDel") MyTweet myTweet) {
		myTweetService.deleteTweet(myTweet.getTweetId());
		ModelAndView mav = new ModelAndView("tweets");
		mav.addObject("tweets", myTweetService.findAllByOrderByTweetIdDesc());
		return mav;
	}

}
