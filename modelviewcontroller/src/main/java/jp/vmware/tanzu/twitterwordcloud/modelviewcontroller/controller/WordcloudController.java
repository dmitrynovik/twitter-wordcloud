package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WordcloudController {

	@GetMapping("/")
	public String Wordcloud() {
		return "wordcloud";
	}

	@GetMapping("/wordcloud-history")
	public String WordcloudHistory() {
		return "wordcloud-history";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

}
