package com.coremedia.labs.plugins.adapters.youtube.connector;

import com.coremedia.labs.plugins.adapters.youtube.connector.playlists.GetPlaylistItemsResponse;
import com.coremedia.labs.plugins.adapters.youtube.connector.playlists.GetPlaylistsResponse;
import com.coremedia.labs.plugins.adapters.youtube.connector.playlists.PlayListService;
import com.coremedia.labs.plugins.adapters.youtube.connector.search.SearchInChannelResponse;
import com.coremedia.labs.plugins.adapters.youtube.connector.search.YouTubeSearchService;
import com.coremedia.labs.plugins.adapters.youtube.connector.videos.VideoService;
import com.coremedia.contenthub.api.search.Sort;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * The YouTubeConnector is responsible for the connection to youtube.
 */
class YouTubeConnectorImpl implements YouTubeConnector {

  private YouTubeSearchService searchService;
  private PlayListService playListService;
  private VideoService videoService;

  YouTubeConnectorImpl(@NonNull YouTubeSearchService searchService,
                       @NonNull PlayListService playListService,
                       @NonNull VideoService videoService) {
    this.searchService = searchService;
    this.playListService = playListService;
    this.videoService = videoService;
  }

  @Override
  @NonNull
  public GetPlaylistsResponse getPlaylistsByChannel(@NonNull String channelId, @Nullable String nextPageCursor) {
    return playListService.fetchPlaylistsForChannel(channelId, nextPageCursor);
  }

  @NonNull
  @Override
  public Optional<Playlist> getPlayList(@NonNull String playListId) {
    return playListService.fetchPlayListById(playListId);
  }

  @Override
  @Nullable
  public Video getVideo(@NonNull String videoId) {
    return videoService.findVideoById(videoId).orElse(null);
  }

  @Override
  @NonNull
  public SearchInChannelResponse getVideos(@NonNull String channelId, @Nullable String pageCursor) {
    try {
      return searchService.searchInChannel(channelId, null, pageCursor);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot fetch videos by channel id " + channelId, e);
    }
  }

  @Override
  @NonNull
  public GetPlaylistItemsResponse getPlaylistItems(@NonNull String playlistId, @Nullable String pageCursor) {
    return playListService.fetchPlayListItems(playlistId, pageCursor);
  }

  @Override
  @NonNull
  public List<SearchResult> searchVideos(@NonNull String channelId, @Nullable String term, @NonNull List<Sort> sortCriteria, int limit) {
    try {
      return searchService.searchInChannel(channelId, term, limit, YouTubeConstants.MAX_LIMIT, sortCriteria);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot fetch videos by channel id " + channelId, e);
    }
  }
}
