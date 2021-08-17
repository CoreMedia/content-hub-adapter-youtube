package com.coremedia.labs.plugins.adapters.youtube.connector.playlists;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Playlist;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface PlayListService {

  /**
   * Fetch the items of the playlist with the given id and the given page cursor.
   * If the pageCursor is null it means that the first page must be returned. Otherwise the page for exactly this cursor
   * must be returned.
   *
   * @param playlistId - the id of the playlist
   * @param pageCursor - null if the first page is requested, otherwise the cursor to the wanted page.
   * @return the response containing the items for that page and the cursor for the next page.
   */
  @NonNull
  GetPlaylistItemsResponse fetchPlayListItems(@NonNull String playlistId, @Nullable String pageCursor);

  @NonNull
  Optional<Playlist> fetchPlayListById(@NonNull String playlistId);

  @NonNull
  GetPlaylistsResponse fetchPlaylistsForChannel(@NonNull String channelId, @Nullable String nextPageCursor);
}
