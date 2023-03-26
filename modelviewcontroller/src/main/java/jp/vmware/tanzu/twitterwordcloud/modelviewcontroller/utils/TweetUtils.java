package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.utils;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twitter.clientlib.model.*;

import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.MyTweet;

public class TweetUtils {

    public static MyTweet getMyTweet(StreamingTweetResponse streamingTweetResponse, Tweet tweet) {
		Expansions expansions = streamingTweetResponse.getIncludes();
		List<User> users = expansions.getUsers();

		User user = users.get(0);

		MyTweet myTweet = new MyTweet();
		myTweet.setTweetId(tweet.getId());
		myTweet.setText(tweet.getText());
		myTweet.setUsername(user.getUsername());
		//myTweet.setCreated(tweet.getCreatedAt());

        return myTweet;
    }

    public static StreamingTweetResponse setStreamTweetResponse(String line) {

		if (line.isEmpty()) {
			return null;
		}

		StreamingTweetResponse streamingTweetResponse = new StreamingTweetResponse();

		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode jsonFullNode;
		try {
			jsonFullNode = objectMapper.readTree(line);
		}
		catch (Exception e) {
			return null;
		}

		JsonNode jsonDataNode = jsonFullNode.get("data");
		JsonNode jsonExpansionNode = jsonFullNode.get("includes");

		if (jsonDataNode != null) {
			Tweet tweet = new Tweet();
			tweet.setId(jsonDataNode.get("id").asText());
			tweet.setText(jsonDataNode.get("text").asText());
			tweet.setLang(jsonDataNode.get("lang").asText());
			streamingTweetResponse.setData(tweet);
		}

		if (jsonExpansionNode != null) {
			JsonNode jsonUserNode = jsonExpansionNode.get("users");
			Expansions expansions = new Expansions();
			if (jsonUserNode != null && jsonUserNode.size() > 0) {
				User user = new User();
				user.setUsername(jsonUserNode.get(0).get("name").asText());
				List<User> users = new ArrayList<>();
				users.add(user);
				expansions.setUsers(users);
			}
			streamingTweetResponse.setIncludes(expansions);
		}

		Tweet tweet = streamingTweetResponse.getData();
		if (tweet == null) {
			return null;
		}

		Expansions expansions = streamingTweetResponse.getIncludes();
		if (expansions == null) {
			return null;
		}
		List<User> users = expansions.getUsers();
		if (users == null) {
			return null;
		}

		return streamingTweetResponse;
	}

}
