package com.coremedia.labs.plugins.adapters.youtube.connector;

import com.coremedia.labs.plugins.adapters.youtube.connector.playlists.GetPlaylistItemsResponse;
import com.coremedia.labs.plugins.adapters.youtube.connector.playlists.GetPlaylistsResponse;
import com.coremedia.labs.plugins.adapters.youtube.connector.search.SearchInChannelResponse;
import com.coremedia.contenthub.api.search.Sort;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface YouTubeConnector {

  @NonNull
  GetPlaylistsResponse getPlaylistsByChannel(@NonNull String channelId, @Nullable String nextPageCursor);

  @NonNull
  Optional<Playlist> getPlayList(@NonNull String playListId);

  @Nullable
  Video getVideo(@NonNull String videoId);

  @NonNull
  SearchInChannelResponse getVideos(@NonNull String channelId, @Nullable String pageCursor);

  @NonNull
  GetPlaylistItemsResponse getPlaylistItems(@NonNull String playlistId, @Nullable String nextPageCursor);

  @NonNull
  List<SearchResult> searchVideos(@NonNull String channelId, @Nullable String term, @NonNull List<Sort> sortCriteria, int limit);
}
