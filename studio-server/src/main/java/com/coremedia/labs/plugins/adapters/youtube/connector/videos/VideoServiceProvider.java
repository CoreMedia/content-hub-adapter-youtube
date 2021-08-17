package com.coremedia.labs.plugins.adapters.youtube.connector.videos;

import com.google.api.services.youtube.YouTube;
import edu.umd.cs.findbugs.annotations.NonNull;

public class VideoServiceProvider {

  @NonNull
  public VideoService getVideoService(@NonNull YouTube youTube) {
    return new VideoServiceImpl(youTube);
  }
}
