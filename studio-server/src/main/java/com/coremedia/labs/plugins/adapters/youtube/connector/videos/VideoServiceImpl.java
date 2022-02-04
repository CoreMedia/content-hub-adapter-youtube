package com.coremedia.labs.plugins.adapters.youtube.connector.videos;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.coremedia.labs.plugins.adapters.youtube.connector.GoogleExceptionUtil.processGoogleJsonResponseException;

class VideoServiceImpl implements VideoService {
  private static final Logger LOG = LoggerFactory.getLogger(VideoServiceImpl.class);

  private static final String REQUEST_TYPE_SNIPPET = "snippet";

  private YouTube youTube;

  public VideoServiceImpl(@NonNull YouTube youTube) {
    this.youTube = youTube;
  }

  @NonNull
  @Override
  public Optional<Video> findVideoById(@NonNull String videoId) {
    LOG.debug("YouTube.videos id: {}", videoId);
    VideoListResponse videoListResponse;
    try {
      videoListResponse = youTube.videos()
              .list(Collections.singletonList(REQUEST_TYPE_SNIPPET))
              .setMaxResults(1L)
              .setId(Collections.singletonList(videoId))
              .execute();
    } catch (GoogleJsonResponseException ge) {
      throw processGoogleJsonResponseException("Cannot fetch video by id " + videoId, ge);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot fetch video by id " + videoId, e);
    }

    List<Video> videos = videoListResponse.getItems();

    if (videos == null) {
      return Optional.empty();
    }

    if (videos.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(videos.get(0));
  }
}
