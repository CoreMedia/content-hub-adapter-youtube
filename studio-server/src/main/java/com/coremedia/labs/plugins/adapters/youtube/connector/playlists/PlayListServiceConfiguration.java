package com.coremedia.labs.plugins.adapters.youtube.connector.playlists;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class PlayListServiceConfiguration {
  @Bean
  PlayListServiceProvider playListServiceProvider() {
    return new PlayListServiceProvider();
  }
}
