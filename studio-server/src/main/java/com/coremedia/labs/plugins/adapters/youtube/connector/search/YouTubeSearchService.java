package com.coremedia.labs.plugins.adapters.youtube.connector.search;

import com.coremedia.labs.plugins.adapters.youtube.YouTubeColumnProvider;
import com.coremedia.contenthub.api.search.Sort;
import com.coremedia.contenthub.api.search.SortDirection;
import com.google.api.services.youtube.model.SearchResult;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface YouTubeSearchService {

  // Origin of these magic constants: https://developers.google.com/youtube/v3/docs
  String YOUTUBE_TYPE_VIDEO = "video";
  String YOUTUBE_LIST_PART_SNIPPET = "snippet";

  // The YouTube API supports exactly these orders.
  // S. https://developers.google.com/youtube/v3/docs/search/list # Optional parameters # order
  String YOUTUBE_ORDER_DATE = "date"; // Descending
  String YOUTUBE_ORDER_TITLE = "title"; // Ascending
  // Currently unused, but possible.  Just for completeness.
  //String YOUTUBE_ORDER_RATING = "rating"; // Descending
  //String YOUTUBE_ORDER_RELEVANCE = "relevance"; // Descending
  //String YOUTUBE_ORDER_VIEWCOUNT = "viewCount"; // Descending

  // The Studio client is not aware of the YouTube wording, but understands
  // the ColumnModelProvider terms.
  Sort STUDIO_SORT_NAME = new Sort(YouTubeColumnProvider.DATA_INDEX_NAME_COLUMN, SortDirection.ASCENDING);
  Sort STUDIO_SORT_LASTMODIFIED = new Sort(YouTubeColumnProvider.DATA_INDEX_LAST_MODIFIED, SortDirection.DESCENDING);

  /**
   * Map the Studio client's column names to the YouTube orders.
   * <p>
   * The Studio client has no access to the unmapped YouTube orders.
   */
  Map<Sort, String> YOUTUBE_VIDEO_ORDERS = Map.of(
          STUDIO_SORT_NAME, YOUTUBE_ORDER_TITLE,
          STUDIO_SORT_LASTMODIFIED, YOUTUBE_ORDER_DATE
                                                                     );
  @NonNull
  List<SearchResult> searchInChannel(@NonNull String channelId,
                                     @Nullable String term,
                                     int limit,
                                     int maxLimit,
                                     @NonNull List<Sort> sortCriteria) throws IOException;

  @NonNull
  SearchInChannelResponse searchInChannel(@NonNull String channelId,
                                          @Nullable String term,
                                          @Nullable String pageCursor) throws IOException;
}
