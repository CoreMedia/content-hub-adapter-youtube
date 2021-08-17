package com.coremedia.labs.plugins.adapters.youtube.connector;

import com.coremedia.labs.plugins.adapters.youtube.YouTubeContentHubSettings;
import edu.umd.cs.findbugs.annotations.NonNull;

public interface YouTubeConnectorFactory {

  @NonNull
  YouTubeConnectorImpl create(@NonNull YouTubeContentHubSettings settings, @NonNull String connectionId);
}
