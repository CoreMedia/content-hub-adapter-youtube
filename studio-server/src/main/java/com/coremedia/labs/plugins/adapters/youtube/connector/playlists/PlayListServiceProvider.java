package com.coremedia.labs.plugins.adapters.youtube.connector.playlists;

import com.google.api.services.youtube.YouTube;
import edu.umd.cs.findbugs.annotations.NonNull;

public class PlayListServiceProvider {
  @NonNull
  public PlayListService getPlayListService(@NonNull YouTube youTube) {
    return new PlayListServiceImpl(youTube);
  }
}
