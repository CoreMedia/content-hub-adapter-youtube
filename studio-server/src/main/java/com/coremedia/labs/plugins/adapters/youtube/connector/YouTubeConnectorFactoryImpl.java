package com.coremedia.labs.plugins.adapters.youtube.connector;

import com.coremedia.labs.plugins.adapters.youtube.YouTubeContentHubSettings;
import com.coremedia.labs.plugins.adapters.youtube.connector.playlists.PlayListService;
import com.coremedia.labs.plugins.adapters.youtube.connector.playlists.PlayListServiceProvider;
import com.coremedia.labs.plugins.adapters.youtube.connector.search.YouTubeSearchService;
import com.coremedia.labs.plugins.adapters.youtube.connector.search.YouTubeSearchServiceProvider;
import com.coremedia.labs.plugins.adapters.youtube.connector.videos.VideoService;
import com.coremedia.labs.plugins.adapters.youtube.connector.videos.VideoServiceProvider;
import com.coremedia.contenthub.api.exception.ContentHubException;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import static com.google.api.client.googleapis.auth.oauth2.GoogleCredential.fromStream;

class YouTubeConnectorFactoryImpl implements YouTubeConnectorFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeConnectorFactoryImpl.class);
  private static final String HTTPS_WWW_GOOGLEAPIS_COM_AUTH_YOUTUBE_FORCE_SSL = "https://www.googleapis.com/auth/youtube.force-ssl";
  private YouTubeSearchServiceProvider youTubeSearchServiceProvider;
  private PlayListServiceProvider playListServiceProvider;
  private VideoServiceProvider videoServiceProvider;

  YouTubeConnectorFactoryImpl(YouTubeSearchServiceProvider youTubeSearchServiceProvider,
                              PlayListServiceProvider playListServiceProvider,
                              VideoServiceProvider videoServiceProvider) {
    this.youTubeSearchServiceProvider = youTubeSearchServiceProvider;
    this.playListServiceProvider = playListServiceProvider;
    this.videoServiceProvider = videoServiceProvider;
  }

  @Override
  @NonNull
  public YouTubeConnectorImpl create(@NonNull YouTubeContentHubSettings settings, @NonNull String connectionId) {
    List<String> scopes = Lists.newArrayList(HTTPS_WWW_GOOGLEAPIS_COM_AUTH_YOUTUBE_FORCE_SSL);
    String credentialsJson = settings.getCredentialsJson();
    if (credentialsJson == null || credentialsJson.length() == 0) {
      throw new ContentHubException("No credentialsJson found for youtube adapter '" + connectionId + "'");
    }

    GoogleCredential credential = null;
    try {
      credential = fromStream(new ByteArrayInputStream(credentialsJson.getBytes()));
    } catch (IOException e) {
      LOGGER.error("Error initializing youtube adapter '" + connectionId + "': " + e.getMessage(), e);
      throw new ContentHubException(e);
    }
    if (credential.createScopedRequired()) {
      credential = credential.createScoped(scopes);
    }
    YouTube youTube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), credential).setApplicationName("youtubeProvider").build();

    YouTubeSearchService searchService = youTubeSearchServiceProvider.getSearchService(youTube);
    PlayListService playListService = playListServiceProvider.getPlayListService(youTube);
    VideoService videoService = videoServiceProvider.getVideoService(youTube);

    return new YouTubeConnectorImpl(searchService, playListService, videoService);
  }
}
