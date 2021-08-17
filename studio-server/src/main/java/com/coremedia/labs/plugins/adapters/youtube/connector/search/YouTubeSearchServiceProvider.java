package com.coremedia.labs.plugins.adapters.youtube.connector.search;

import com.google.api.services.youtube.YouTube;
import edu.umd.cs.findbugs.annotations.NonNull;

public class YouTubeSearchServiceProvider {

  @NonNull
  public YouTubeSearchService getSearchService(@NonNull YouTube youTube) {
    return new YouTubeSearchServiceImpl(youTube);
  }
}
