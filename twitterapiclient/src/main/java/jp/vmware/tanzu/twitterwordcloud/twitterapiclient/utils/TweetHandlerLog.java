package jp.vmware.tanzu.twitterwordcloud.twitterapiclient.utils;

import jp.vmware.tanzu.twitterwordcloud.library.utils.TweetHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "log.mode", havingValue = "true")
public class TweetHandlerLog implements TweetHandler {

	@Override
	public void handle(String tweet) {
		System.out.println(tweet);
	}

}
