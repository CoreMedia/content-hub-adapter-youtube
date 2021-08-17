package com.coremedia.labs.plugins.adapters.youtube.connector;

import com.coremedia.labs.plugins.adapters.youtube.connector.playlists.PlayListServiceConfiguration;
import com.coremedia.labs.plugins.adapters.youtube.connector.playlists.PlayListServiceProvider;
import com.coremedia.labs.plugins.adapters.youtube.connector.search.YouTubeSearchConfiguration;
import com.coremedia.labs.plugins.adapters.youtube.connector.search.YouTubeSearchServiceProvider;
import com.coremedia.labs.plugins.adapters.youtube.connector.videos.VideoServiceConfiguration;
import com.coremedia.labs.plugins.adapters.youtube.connector.videos.VideoServiceProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@Import({YouTubeSearchConfiguration.class,
         PlayListServiceConfiguration.class,
         VideoServiceConfiguration.class})
public class YouTubeConnectorConfiguration {
  @Bean
  public YouTubeConnectorFactory youTubeConnectorFactory(YouTubeSearchServiceProvider youTubeSearchServiceProvider,
                                                         PlayListServiceProvider playListServiceProvider,
                                                         VideoServiceProvider videoServiceProvider) {
    return new YouTubeConnectorFactoryImpl(youTubeSearchServiceProvider, playListServiceProvider, videoServiceProvider);
  }
}
