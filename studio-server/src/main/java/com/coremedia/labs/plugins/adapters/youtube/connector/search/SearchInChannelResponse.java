package com.coremedia.labs.plugins.adapters.youtube.connector.search;

import com.google.api.services.youtube.model.SearchResult;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;

public class SearchInChannelResponse {

  private List<SearchResult> results;
  private String nextPageToken;

  SearchInChannelResponse(@NonNull List<SearchResult> results,
                          @Nullable String nextPageToken) {
    this.results = results;
    this.nextPageToken = nextPageToken;
  }

  @NonNull
  public List<SearchResult> getResults() {
    return results;
  }

  @Nullable
  public String getNextPageToken() {
    return nextPageToken;
  }
}
