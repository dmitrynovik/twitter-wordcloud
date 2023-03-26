package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model;

import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.geode.core.util.ObjectUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

//@Region("Tweets")
@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class CachedTweet {
    
	public String id;

	public String text;

	public String username;

	public int created;

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof CachedTweet)) {
			return false;
		}

		CachedTweet that = (CachedTweet) obj;

		return ObjectUtils.nullSafeEquals(this.id, that.id);
	}

	@Override
	public int hashCode() {

		int hashValue = 17;

		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(id);

		return hashValue;
	}

	@Override
	public String toString() {
		return text;
	}

}