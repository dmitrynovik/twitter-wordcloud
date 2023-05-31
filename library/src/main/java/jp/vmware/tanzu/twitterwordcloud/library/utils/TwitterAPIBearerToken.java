package jp.vmware.tanzu.twitterwordcloud.library.utils;

public class TwitterAPIBearerToken {
    
    public static String retrieveToken() {
        final String varname = "TWITTER_API_BEARER_TOKEN";
        String twitterBearerToken = System.getenv(varname);
		if (twitterBearerToken == null || twitterBearerToken.isEmpty())
			throw new AssertionError(varname + " environment variable is not set", null);

        return twitterBearerToken;
    }
}
