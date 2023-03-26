package jp.vmware.tanzu.twitterwordcloud.modelviewcontroller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.cache.config.EnableGemfireCaching;
import org.springframework.data.gemfire.config.annotation.*;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication.Locator;

import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.model.MyTweet;
import jp.vmware.tanzu.twitterwordcloud.modelviewcontroller.service.HistoryService;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.boot.ApplicationRunner;


@SpringBootApplication(
    scanBasePackages = "jp.vmware.tanzu.twitterwordcloud"
    ,exclude = {
        org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
        org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration.class
    }
    )
@ClientCacheApplication
@EnableGemfireCaching
@EnablePdx
//@EnableEntityDefinedRegions(clientRegionShortcut = ClientRegionShortcut.PROXY, basePackageClasses = MyTweet.class)
@EnableCachingDefinedRegions(clientRegionShortcut = ClientRegionShortcut.PROXY)
@EnableClusterConfiguration(useHttp = true, requireHttps = false)
@SuppressWarnings("unused")
public class ModelViewControllerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModelViewControllerApplication.class, args);
	}

	// @Bean
    // ApplicationRunner runAdditionalClientCacheInitialization(GemFireCache gemfireCache) {

    //     return args -> {
    //         ClientCache clientCache = (ClientCache) gemfireCache;
	// 		// perform additional ClientCache initialization as needed
    //     };
    // }

  @Bean
  ApplicationRunner runner(HistoryService historyService) {
    return args -> {
            historyService.readFromRabbitMQStream();

            // var tweet = historyService.getTweet("1631476990065745920");
            // System.out.println(" !!! TWEET: " + tweet.text);
        };
    }

}
