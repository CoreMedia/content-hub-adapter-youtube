package com.coremedia.labs.plugins.adapters.youtube;

import com.coremedia.labs.plugins.adapters.youtube.connector.YouTubeConnector;
import com.coremedia.labs.plugins.adapters.youtube.connector.YouTubeConstants;
import com.coremedia.labs.plugins.adapters.youtube.connector.playlists.GetPlaylistItemsResponse;
import com.coremedia.labs.plugins.adapters.youtube.connector.playlists.GetPlaylistsResponse;
import com.coremedia.labs.plugins.adapters.youtube.connector.search.SearchInChannelResponse;
import com.coremedia.labs.plugins.adapters.youtube.connector.search.YouTubeSearchService;
import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubContext;
import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubTransformer;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Folder;
import com.coremedia.contenthub.api.GetChildrenResult;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.column.ColumnProvider;
import com.coremedia.contenthub.api.exception.ContentHubException;
import com.coremedia.contenthub.api.pagination.PaginationRequest;
import com.coremedia.contenthub.api.pagination.PaginationResponse;
import com.coremedia.contenthub.api.search.ContentHubSearchResult;
import com.coremedia.contenthub.api.search.ContentHubSearchService;
import com.coremedia.contenthub.api.search.Sort;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class YouTubeContentHubAdapter implements ContentHubAdapter, ContentHubSearchService {

  private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeContentHubAdapter.class);
  private static final List<ContentHubType> SEARCH_TYPES = Collections.singletonList(new ContentHubType(YouTubeTypes.ITEM));
  public static final String VIDEOS_PAGE_TOKEN_PREFIX = "videos_";

  private final YouTubeContentHubSettings settings;
  private final String connectionId;
  private final YouTubeColumnProvider columnProvider;
  private final YouTubeConnector youTubeConnector;
  private final ContentHubObjectId rootId;

  YouTubeContentHubAdapter(YouTubeConnector youTubeConnector,
                           YouTubeContentHubSettings settings, String connectionId) {
    this.youTubeConnector = youTubeConnector;
    this.settings = settings;
    this.connectionId = connectionId;
    rootId = new ContentHubObjectId(connectionId, connectionId);
    columnProvider = new YouTubeColumnProvider();
  }


  // --- ContentHubAdapter ------------------------------------------

  @NonNull
  @Override
  public Folder getRootFolder(@NonNull ContentHubContext context) throws ContentHubException {
    return new YouTubeFolder(rootId, getChannelDisplayName());
  }

  @Nullable
  @Override
  public Item getItem(@NonNull ContentHubContext context, @NonNull ContentHubObjectId id) throws ContentHubException {
    try {
      return item(id);
    } catch (Exception e) {
      throw new ContentHubException("Cannot get item by id " + id, e);
    }
  }

  @Nullable
  @Override
  public Folder getFolder(@NonNull ContentHubContext context, @NonNull ContentHubObjectId id) throws ContentHubException {
    if (rootId.equals(id)) {
      return getRootFolder(context);
    }
    return youTubeConnector.getPlayList(id.getExternalId())
            .map(playlist -> {
              ContentHubObjectId categoryId = new ContentHubObjectId(connectionId, playlist.getId());
              return new YouTubeFolder(categoryId, playlist);
            })
            .orElse(null);
  }

  @NonNull
  @Override
  public GetChildrenResult getChildren(@NonNull ContentHubContext context, @NonNull Folder folder, @Nullable PaginationRequest paginationRequest) {
    String pageCursor = paginationRequest == null ? null : paginationRequest.getNextPageCursor();
    if (isRootFolder(folder)) {
      return getChildrenOfRootFolder(pageCursor);
    }

    return getChildrenOfPlaylist(folder, pageCursor);
  }

  @NonNull
  private ContentHubObject toContentHubObject(@NonNull Playlist playlist) {
    ContentHubObjectId categoryId = new ContentHubObjectId(connectionId, playlist.getId());
    return new YouTubeFolder(categoryId, playlist);
  }

  @NonNull
  private GetChildrenResult getChildrenOfRootFolder(@Nullable String pageCursor) {
    String channelId = getChannelId(settings);
    List<ContentHubObject> playlists = null;
    if(pageCursor == null || !pageCursor.startsWith(VIDEOS_PAGE_TOKEN_PREFIX)){
    GetChildrenResult playlistsResult = getPlaylists(pageCursor);
      playlists = playlistsResult.getChildren();
      boolean pageIsFull = playlists.size() == YouTubeConstants.LOW_COST_REQUEST_PAGE_SIZE;
      if (pageIsFull) {
        return playlistsResult;
      }
    }

    boolean noPlaylistsFound = playlists == null || playlists.isEmpty();
    if (noPlaylistsFound) {
      String videoPageCursor = pageCursor == null ? null : pageCursor.replace(VIDEOS_PAGE_TOKEN_PREFIX, "");
      return getVideosPaginated(channelId, videoPageCursor);
    }

    List<ContentHubObject> mergeResults = new ArrayList<>(playlists);
    GetChildrenResult videoResult = getVideosPaginated(channelId, null);
    mergeResults.addAll(videoResult.getChildren());

    return new GetChildrenResult(mergeResults, videoResult.getPaginationResponse());
  }

  @NonNull
  private GetChildrenResult getVideosPaginated(@NonNull String channelId, @Nullable String pageCursor) {
    SearchInChannelResponse videos = youTubeConnector.getVideos(channelId, pageCursor);
    List<ContentHubObject> children = videos.getResults().stream().map(this::item).collect(Collectors.toList());
    return new GetChildrenResult(children, new PaginationResponse(videos.getNextPageToken() != null, VIDEOS_PAGE_TOKEN_PREFIX + videos.getNextPageToken()));
  }

  @NonNull
  private GetChildrenResult getChildrenOfPlaylist(@NonNull Folder folder, @Nullable String pageCursor) {
    GetPlaylistItemsResponse playlistItems = youTubeConnector.getPlaylistItems(folder.getId().getExternalId(), pageCursor);
    List<ContentHubObject> children = playlistItems.getPlaylistItems().stream().map(this::item).collect(Collectors.toList());
    return new GetChildrenResult(children, new PaginationResponse(playlistItems.getNextPageCursor() != null, playlistItems.getNextPageCursor()));
  }

  @Nullable
  @Override
  public Folder getParent(@NonNull ContentHubContext context, @NonNull ContentHubObject contentHubObject) throws ContentHubException {
    return rootId.equals(contentHubObject.getId()) ? null : getRootFolder(context);
  }

  @Override
  @NonNull
  public ContentHubTransformer transformer() {
    return new YouTubeContentHubTransformer();
  }

  @NonNull
  @Override
  public Optional<ContentHubSearchService> searchService() {
    return Optional.of(this);
  }


  // --- ContentHubSearchService ------------------------------------

  @Override
  @NonNull
  public Collection<ContentHubType> supportedTypes() {
    return SEARCH_TYPES;
  }

  @Override
  @NonNull
  public Set<Sort> supportedSortCriteria() {
    return YouTubeSearchService.YOUTUBE_VIDEO_ORDERS.keySet();
  }

  @Override
  public int supportedLimit() {
    return YouTubeConstants.MAX_LIMIT;
  }

  @Override
  @NonNull
  public ContentHubSearchResult search(@NonNull String query,
                                       @Nullable Folder belowFolder,
                                       @Nullable ContentHubType type,
                                       @NonNull Collection<String> filterQueries,
                                       @NonNull List<Sort> sortCriteria,
                                       int limit) {
    if (belowFolder != null) {
      throw new IllegalArgumentException("Search below folder is not supported");
    }
    if (type != null && !supportedTypes().contains(type)) {
      throw new IllegalArgumentException("Unsupported search type " + type);
    }
    if (limit < -1) {
      throw new IllegalArgumentException("limit must be >= -1, as specified by ContentHubSearchService.search.");
    }
    if (limit == 0) {
      LOGGER.debug("YouTube does not support total hits, result is useless.");
      return new ContentHubSearchResult(Collections.emptyList());
    }
    if (limit > supportedLimit()) {
      // Be gentle for now, since we have not decided yet how to deal with the
      // limit in the UI.  Maybe, we throw an IllegalArgumentException later.
      LOGGER.debug("{} is greater than the supported limit of {}, result may be misleading", limit, supportedLimit());
    }
    if (!filterQueries.isEmpty()) {
      LOGGER.debug("filterQueries are not supported, ignore.");
    }

    try {
      List<SearchResult> videos = youTubeConnector.searchVideos(settings.getChannelId(), query, sortCriteria, limit);
      List<Item> items = videos.stream().map(this::item).collect(Collectors.toUnmodifiableList());
      return new ContentHubSearchResult(items);
    } catch (Exception e) {
      throw new ContentHubException("YouTube search failed", e);
    }
  }

  //------------------------ Helper ------------------------------------------------------------------------------------

  private Item item(@NonNull ContentHubObjectId id) {
    Video video = youTubeConnector.getVideo(id.getExternalId());
    return video != null ? new YouTubeItem(id, video) : null;
  }

  private Item item(PlaylistItem playlistItem) {
    String videoId = playlistItem.getSnippet().getResourceId().getVideoId();
    ContentHubObjectId objectId = new ContentHubObjectId(connectionId, videoId);
    return new YouTubeItem(objectId, playlistItem);
  }

  private Item item(SearchResult searchResult) {
    String videoId = searchResult.getId().getVideoId();
    ContentHubObjectId objectId = new ContentHubObjectId(connectionId, videoId);
    return new YouTubeItem(objectId, searchResult);
  }

  private String getChannelDisplayName() {
    String name = settings.getDisplayName();
    if (StringUtils.isEmpty(name)) {
      LOGGER.warn("No display name set for YouTube adapter '{}'", connectionId);
      name = connectionId;
    }
    return name;
  }

  @NonNull
  private GetChildrenResult getPlaylists(@Nullable String pageCursor) {
    String channelId = getChannelId(settings);
    if (StringUtils.isEmpty(channelId)) {
      return new GetChildrenResult(Collections.emptyList());
    }

    GetPlaylistsResponse playlistsResponse = youTubeConnector.getPlaylistsByChannel(channelId, pageCursor);
    String nextPage = playlistsResponse.getNextPageCursor();
    List<Playlist> children = playlistsResponse.getPlaylists();
    List<ContentHubObject> asObjects = children.stream().map(this::toContentHubObject).collect(Collectors.toList());
    return new GetChildrenResult(asObjects, new PaginationResponse(nextPage != null, nextPage));
  }

  @NonNull
  private String getChannelId(@Nullable YouTubeContentHubSettings settings) {

    if (settings == null) {
      throw new IllegalStateException("No channelId to fetch items for");
    }

    String channelId = settings.getChannelId();
    if (StringUtils.isEmpty(channelId)) {
      throw new IllegalStateException("No channelId to fetch items for");
    }

    return channelId;
  }

  private boolean isRootFolder(@NonNull Folder folder) {
    return rootId.equals(folder.getId());
  }

  @NonNull
  @Override
  public ColumnProvider columnProvider() {
    return columnProvider;
  }
}
