package com.coremedia.labs.plugins.adapters.youtube.connector;

public interface YouTubeConstants {
  /**
   * The maximum number of search hits supported by the YouTube API.
   * <p>
   * S. https://developers.google.com/youtube/v3/docs/search/list # Optional parameters # channelId
   * <p>
   */
  int MAX_LIMIT = 500;

  // Page sizes
  /*
   * @implNote 50 is the max value.  Don't know why we should fetch less and do
   * more roundtrips instead.  However, default is suspiciously low 5.
   * So maybe I understood something wrong, and 50 is not appropriate.
   * S. https://developers.google.com/youtube/v3/docs/search/list # Optional parameters # maxResults
   */

  /**
   * The page size for requests which cost a lot of quota points.
   */
  long HIGH_COST_REQUEST_PAGE_SIZE = 50L;

  /**
   * The page size for requests which don't cost a lot of quota points.
   */
  long LOW_COST_REQUEST_PAGE_SIZE = 30;
}
