package com.coremedia.labs.plugins.adapters.youtube;

import com.coremedia.labs.plugins.adapters.youtube.connector.YouTubeConnectorConfiguration;
import com.coremedia.labs.plugins.adapters.youtube.connector.YouTubeConnectorFactory;
import com.coremedia.contenthub.api.ContentHubAdapterFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import(YouTubeConnectorConfiguration.class)
public class YouTubeConfiguration {
  @Bean
  public ContentHubAdapterFactory youTubeContentHubAdapterFactory(YouTubeConnectorFactory youTubeConnectorFactory) {
    return new YouTubeContentHubAdapterFactory(youTubeConnectorFactory);
  }
}
