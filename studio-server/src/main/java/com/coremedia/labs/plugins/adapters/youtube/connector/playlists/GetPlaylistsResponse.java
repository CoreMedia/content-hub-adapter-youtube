package com.coremedia.labs.plugins.adapters.youtube.connector.playlists;

import com.google.api.services.youtube.model.Playlist;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;

public class GetPlaylistsResponse {

  private List<Playlist> playlists;
  private String nextPageCursor;

  GetPlaylistsResponse(@NonNull List<Playlist> playlists,
                       @Nullable String nextPageCursor) {
    this.playlists = playlists;
    this.nextPageCursor = nextPageCursor;
  }

  @NonNull
  public List<Playlist> getPlaylists() {
    return playlists;
  }

  @Nullable
  public String getNextPageCursor() {
    return nextPageCursor;
  }
}
