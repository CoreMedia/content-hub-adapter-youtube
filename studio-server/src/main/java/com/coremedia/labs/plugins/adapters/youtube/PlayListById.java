package com.coremedia.labs.plugins.adapters.youtube;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

class PlayListById {
  private static final Logger LOG = LoggerFactory.getLogger(PlayListById.class);
  private static final String SNIPPET = "snippet";

  private final YouTube youTube;
  private final String id;

  PlayListById(YouTube youTube, String id) {
    this.youTube = youTube;
    this.id = id;
  }

  Playlist playlist() throws IOException {
    return fetchPlayListById();
  }

  private Playlist fetchPlayListById() throws IOException {
    LOG.debug("YouTube.Playlist id: {}", id);
    PlaylistListResponse playlistListResponse = youTube.playlists()
            .list(Collections.singletonList(SNIPPET))
            .setId(Collections.singletonList(id))
            .execute();

    return playlistListResponse.getItems().stream()
            .findAny()
            .orElse(null);
  }
}
