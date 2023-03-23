package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.clientlib.model.Expansions;
import com.twitter.clientlib.model.StreamingTweetResponse;
import com.twitter.clientlib.model.Tweet;
import com.twitter.clientlib.model.User;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.MyTweet;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.TweetText;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.repository.MyTweetRepository;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.repository.TweetTextRepository;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.utils.MorphologicalAnalysis;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.utils.TweetUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TweetStreamService {

	private static final Logger logger = LoggerFactory.getLogger(TweetStreamService.class);

	private final List<SseEmitter> emitters;

	public MyTweetRepository myTweetRepository;

	public TweetTextRepository tweetTextRepository;

	public MorphologicalAnalysis morphologicalAnalysis;

	public String lang;

	Pattern nonLetterPattern;

	public TweetStreamService(MyTweetRepository myTweetRepository, TweetTextRepository tweetTextRepository,
			MorphologicalAnalysis morphologicalAnalysis, @Value("${twitter.search.lang}") String lang) {
		this.myTweetRepository = myTweetRepository;
		this.tweetTextRepository = tweetTextRepository;
		this.lang = lang;
		this.morphologicalAnalysis = morphologicalAnalysis;
		this.emitters = new CopyOnWriteArrayList<>();
		this.nonLetterPattern = Pattern.compile("^\\W+$", Pattern.UNICODE_CHARACTER_CLASS);
	}

	public List<SseEmitter> getEmitters() {
		return emitters;
	}

	@Transactional
	public void handler(String line) throws InterruptedException {

		StreamingTweetResponse streamingTweetResponse = TweetUtils.setStreamTweetResponse(line);

		if (streamingTweetResponse == null) {
			Thread.sleep(100);
			return;
		}

		Tweet tweet = streamingTweetResponse.getData();
		MyTweet myTweet = TweetUtils.getMyTweet(streamingTweetResponse, tweet);

		// if (!langSupported(streamingTweetResponse)) {
		// 	return;
		// }

		logger.debug("Handling Tweet : " + myTweet.getText());
		myTweetRepository.save(myTweet);

		boolean nextSkip = false;
		
		for (String text : morphologicalAnalysis.getToken(tweet.getText())) {

			TweetText tweetText = new TweetText();

			// Skip until blank character when hast tag or username tag found
			if (nextSkip) {
				if (text.isBlank()) {
					nextSkip = false;
				}
				continue;
			}
			// Skip hashtag and username and also set next skip to true
			if (text.equals("#") || text.equals("@")) {
				nextSkip = true;
				continue;
			}
			// Skip RT, blank, and non letter words
			Matcher m = nonLetterPattern.matcher(text);
			if (text.equals("RT") || text.isBlank() || m.matches()) {
				continue;
			}

			tweetText.setTweetId(tweet.getId());
			tweetText.setText(text);

			tweetTextRepository.save(tweetText);

		}

	}

	private boolean langSupported(StreamingTweetResponse streamingTweetResponse) {
		Tweet tweet = streamingTweetResponse.getData();
		return tweet != null && tweet.getLang().equals(lang);
	}

	public void notifyTweetEvent(String line) {
		StreamingTweetResponse streamingTweetResponse = TweetUtils.setStreamTweetResponse(line);

		if (streamingTweetResponse == null) {
			return;
		}

		// if (!langSupported(streamingTweetResponse)) {
		// 	return;
		// }

		Tweet tweet = streamingTweetResponse.getData();

		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(SseEmitter.event().name("newTweet").data("New Tweet Arrived : " + tweet.getText()));
			}
			catch (IOException e) {
				logger.warn("Failed to send SSE :" + e);
			}
		}
	}
}
