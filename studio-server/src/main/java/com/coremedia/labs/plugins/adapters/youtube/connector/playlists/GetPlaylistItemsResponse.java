package com.coremedia.labs.plugins.adapters.youtube.connector.playlists;

import com.google.api.services.youtube.model.PlaylistItem;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

public class GetPlaylistItemsResponse {
  private List<PlaylistItem> playlistItems;
  private String nextPageCursor;

  GetPlaylistItemsResponse(@NonNull List<PlaylistItem> playlistItems,
                           @NonNull String nextPageCursor) {
    this.playlistItems = playlistItems;
    this.nextPageCursor = nextPageCursor;
  }

  public List<PlaylistItem> getPlaylistItems() {
    return playlistItems;
  }

  public String getNextPageCursor() {
    return nextPageCursor;
  }
}
