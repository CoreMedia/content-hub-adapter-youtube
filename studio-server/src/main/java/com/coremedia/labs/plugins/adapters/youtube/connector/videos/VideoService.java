package com.coremedia.labs.plugins.adapters.youtube.connector.videos;

import com.google.api.services.youtube.model.Video;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Optional;

public interface VideoService {

  @NonNull
  Optional<Video> findVideoById(@NonNull String videoId);
}
