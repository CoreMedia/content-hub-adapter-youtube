package com.coremedia.labs.plugins.adapters.youtube;


import com.coremedia.common.util.WordAbbreviator;
import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.google.api.client.util.DateTime;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.Calendar;

abstract class YouTubeHubObject implements ContentHubObject {

  private static final WordAbbreviator ABBREVIATOR = new WordAbbreviator();

  private final ContentHubObjectId hubId;

  YouTubeHubObject(@NonNull ContentHubObjectId hubId) {
    this.hubId = hubId;
  }

  @NonNull
  @Override
  public String getDisplayName() {
    return getName();
  }

  @NonNull
  @Override
  public ContentHubObjectId getId() {
    return hubId;
  }

  // --- internal ---------------------------------------------------

  @Nullable
  String formatPreviewString(@Nullable String str) {
    return str==null ? null : ABBREVIATOR.abbreviateString(str, 240);
  }

  @Nullable
  Object formatPreviewDate(@Nullable DateTime date) {
    if (date == null) {
      return null;
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(date.getValue());
    return calendar;
  }

  static String titleToName(@Nullable String title) {
    return title!=null ? title : "-";
  }
}
