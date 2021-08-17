package com.coremedia.labs.plugins.adapters.youtube.connector.search;

import com.coremedia.labs.plugins.adapters.youtube.connector.YouTubeConstants;
import com.coremedia.contenthub.api.search.Sort;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class YouTubeSearchServiceImpl implements YouTubeSearchService {
  private static final Logger LOG = LoggerFactory.getLogger(YouTubeSearchServiceImpl.class);

  private YouTube youTube;

  YouTubeSearchServiceImpl(YouTube youTube) {
    this.youTube = youTube;
  }

  /**
   * @implNote We need the nullable term to fetch the "videos of the root folder".
   * Since YouTube has no folder model, this means all videos of the channel, and
   * there is no cheaper YouTube API way but search to obtain them.
   */
  @Override
  @NonNull
  public List<SearchResult> searchInChannel(@NonNull String channelId,
                                            @Nullable String term,
                                            int limit,
                                            int maxLimit,
                                            @NonNull List<Sort> sortCriteria) throws IOException {
    //ensure that it is null see implNote
    String possiblyNulledTerm = StringUtils.isEmpty(term) ? null : term;

    String order = studioSortToYoutubeOrder(sortCriteria);
    int maxRespectedLimit = limitToLimit(limit, maxLimit);

    List<SearchResult> result = new ArrayList<>();
    fetchSearchListRecursively(channelId, possiblyNulledTerm, order, maxRespectedLimit, result, null);
    return result;
  }

  @NonNull
  @Override
  public SearchInChannelResponse searchInChannel(@NonNull String channelId,
                                                 @Nullable String term,
                                                 @Nullable String pageCursor) throws IOException {
    //ensure that it is null see implNote
    String possiblyNulledTerm = StringUtils.isEmpty(term) ? null : term;
    return fetchSearchList(channelId, possiblyNulledTerm, pageCursor);
  }


  // --- internal ---------------------------------------------------

  /**
   * Fetch the results.
   *
   * @implNote <ul>
   *   <li>YouTube supports no limit, must count ourselves (s. limit)</li>
   *   <li>Responses are paginated, method is recursive (s. token)</li>
   * </ul>
   */
  private void fetchSearchListRecursively(@NonNull String channelId,
                                          @Nullable String term,
                                          String order,
                                          int limit,
                                          List<SearchResult> searchResults, String token) throws IOException {
    LOG.debug("YouTube.search channel: {}, term: {}, order: {}, limit: {}{}", channelId, term, order, limit, (token==null ? "" : " (paging)"));
    YouTube.Search.List list = youTube.search()
            .list(YOUTUBE_LIST_PART_SNIPPET)
            .setMaxResults(YouTubeConstants.HIGH_COST_REQUEST_PAGE_SIZE)
            .setChannelId(channelId)
            .setType(YOUTUBE_TYPE_VIDEO);
    if (term != null) {
      list.setQ(term);
    }
    if (order != null) {
      list.setOrder(order);
    }
    if (token != null) {
      list.setPageToken(token);
    }
    SearchListResponse response = list.execute();

    List<SearchResult> items = response.getItems();
    if (items != null) {
      int missing = limit==-1 ? Integer.MAX_VALUE : limit-searchResults.size();
      searchResults.addAll(items.subList(0, Math.min(missing, items.size())));
    }

    if (limit==-1 || searchResults.size() < limit) {
      String nextPageToken = response.getNextPageToken();
      if (nextPageToken != null) {
        fetchSearchListRecursively(channelId, term, order, limit, searchResults, nextPageToken);
      }
    }
  }
  private SearchInChannelResponse fetchSearchList(@NonNull String channelId,
                                                  @Nullable String term,
                                                  String token) throws IOException {
    LOG.debug("YouTube.search channel: {}, term: {},{}", channelId, term, (token==null ? "" : " (paging)"));
    YouTube.Search.List list = youTube.search()
                                      .list(YOUTUBE_LIST_PART_SNIPPET)
                                      .setMaxResults(YouTubeConstants.HIGH_COST_REQUEST_PAGE_SIZE)
                                      .setChannelId(channelId)
                                      .setType(YOUTUBE_TYPE_VIDEO);
    if (term != null) {
      list.setQ(term);
    }
    if (token != null) {
      list.setPageToken(token);
    }
    SearchListResponse response = list.execute();

    List<SearchResult> items = response.getItems();
    String nextPageToken = response.getNextPageToken();
    return new SearchInChannelResponse(items, nextPageToken);
  }

  private static String studioSortToYoutubeOrder(List<Sort> sortCriteria) {
    if (sortCriteria.isEmpty()) {
      return null;
    }
    Sort studioSort = sortCriteria.get(0);
    if (sortCriteria.size() > 1) {
      // (Almost) silently ignore this illegal argument.
      // The Studio user can only select one order anyway, so he will not
      // notice ineffective fallback Sorts added by the Studio client.
      LOG.debug("YouTube does not support cascaded sorting.  Only {} is effective.", studioSort);
    }
    String youtubeOrder = YOUTUBE_VIDEO_ORDERS.get(studioSort);
    if (youtubeOrder == null) {
      throw new IllegalArgumentException("No YouTube video order for: " + studioSort);
    }
    return youtubeOrder;
  }

  private static int limitToLimit(int limit, int maxLimit) {
    if (limit > maxLimit) {
      LOG.debug("Limit {} is overridden by YouTube's limit of 500.", limit);
    }
    // Preserve our value, anyway.  Code can cope with it.
    return limit;
  }
}
