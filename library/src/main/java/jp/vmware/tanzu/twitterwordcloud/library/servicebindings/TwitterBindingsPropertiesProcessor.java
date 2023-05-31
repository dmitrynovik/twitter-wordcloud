package jp.vmware.tanzu.twitterwordcloud.library.servicebindings;

import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.boot.BindingsPropertiesProcessor;
import org.springframework.core.env.Environment;

import jp.vmware.tanzu.twitterwordcloud.library.utils.TwitterAPIBearerToken;

import java.util.List;
import java.util.Map;

public class TwitterBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

	public static final String TYPE = "twitter";

	@Override
	public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
		if (!environment.getProperty("jp.vmware.tanzu.bindings.boot.twitter.enable", Boolean.class, true)) {
			return;
		}
		List<Binding> myBindings = bindings.filterBindings(TYPE);
		if (myBindings.size() == 0) {
			return;
		}

		

		properties.put("twitter.bearer.token", TwitterAPIBearerToken.retrieveToken());
		properties.put("management.endpoint.health.group.liveness.include", "livenessState,twitterClient");
		properties.put("management.endpoint.health.group.liveness.additional-path", "server:/livez");
	}

}
