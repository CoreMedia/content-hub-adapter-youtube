package com.coremedia.labs.plugins.adapters.youtube.connector.playlists;

import com.coremedia.labs.plugins.adapters.youtube.connector.YouTubeConstants;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistListResponse;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.coremedia.labs.plugins.adapters.youtube.connector.GoogleExceptionUtil.processGoogleJsonResponseException;

class PlayListServiceImpl implements PlayListService {
  private static final Logger LOG = LoggerFactory.getLogger(PlayListServiceImpl.class);
  private static final String REQUEST_TYPE_SNIPPET = "snippet";

  private YouTube youTube;

  PlayListServiceImpl(YouTube youTube) {
    this.youTube = youTube;
  }

  @Override
  @NonNull
  public GetPlaylistItemsResponse fetchPlayListItems(@NonNull String playlistId, @Nullable String pageCursor) {
    try {
      LOG.debug("YouTube.PlaylistItems id: {}{}", playlistId, (pageCursor == null ? "" : " (paging)"));
      YouTube.PlaylistItems.List list = youTube.playlistItems()
              .list(Collections.singletonList(REQUEST_TYPE_SNIPPET))
              .setMaxResults(YouTubeConstants.LOW_COST_REQUEST_PAGE_SIZE)
              .setPlaylistId(playlistId);

      if (pageCursor != null) {
        list.setPageToken(pageCursor);
      }

      PlaylistItemListResponse response = list.execute();
      List<PlaylistItem> responseItems = response.getItems();
      String nextPageToken = response.getNextPageToken();
      return new GetPlaylistItemsResponse(responseItems, nextPageToken);
    } catch (GoogleJsonResponseException ge) {
      throw processGoogleJsonResponseException("Cannot fetch playlist items by id " + playlistId, ge);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot fetch playlist items by id " + playlistId, e);
    }
  }

  @NonNull
  @Override
  public Optional<Playlist> fetchPlayListById(@NonNull String playlistId) {
    try {
      LOG.debug("YouTube.Playlist id: {}", playlistId);
      PlaylistListResponse playlistListResponse = youTube.playlists()
              .list(Collections.singletonList(REQUEST_TYPE_SNIPPET))
              .setId(Collections.singletonList(playlistId))
              .execute();

      return playlistListResponse.getItems().stream().findAny();
    } catch (GoogleJsonResponseException ge) {
      throw processGoogleJsonResponseException("Cannot fetch playlist by id " + playlistId, ge);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot fetch playlist by id " + playlistId, e);
    }

  }

  @Override
  @NonNull
  public GetPlaylistsResponse fetchPlaylistsForChannel(@NonNull String channelId, @Nullable String pageCursor) {
    try {
      LOG.debug("YouTube.Playlists channel: {}{}", channelId, (pageCursor == null ? "" : " (paging)"));
      YouTube.Playlists.List list = youTube.playlists()
              .list(Collections.singletonList(REQUEST_TYPE_SNIPPET))
              .setMaxResults(YouTubeConstants.LOW_COST_REQUEST_PAGE_SIZE)
              .setChannelId(channelId);

      if (pageCursor != null) {
        list.setPageToken(pageCursor);
      }

      PlaylistListResponse response = list.execute();
      List<Playlist> items = response.getItems();
      String nextPageToken = response.getNextPageToken();
      return new GetPlaylistsResponse(items, nextPageToken);
    } catch (GoogleJsonResponseException ge) {
      throw processGoogleJsonResponseException("Cannot fetch playLists by channel id " + channelId, ge);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot fetch playLists by channel id " + channelId, e);
    }
  }
}
