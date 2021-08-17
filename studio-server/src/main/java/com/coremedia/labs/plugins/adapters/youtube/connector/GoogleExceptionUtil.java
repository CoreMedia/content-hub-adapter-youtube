package com.coremedia.labs.plugins.adapters.youtube.connector;

import com.coremedia.labs.plugins.adapters.youtube.YouTubeErrorCode;
import com.coremedia.contenthub.api.exception.ContentHubException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.List;

public class GoogleExceptionUtil {
  public static RuntimeException processGoogleJsonResponseException(@NonNull String errorMessage, GoogleJsonResponseException ge) {
    boolean usageLimits = ge.getDetails().getErrors().stream()
            .anyMatch(error -> "dailyLimitExceeded".equals(error.getReason()));

    if (usageLimits) {
      return new ContentHubException("Daily limit exceeded", ge, YouTubeErrorCode.USAGE_LIMIT_EXCEEDED, List.of(ge.getDetails().getMessage()));
    }

    boolean quotaExceeded = ge.getDetails().getErrors().stream()
            .anyMatch(error -> "quotaExceeded".equals(error.getReason()));
    if (quotaExceeded) {
      return new ContentHubException("Quota points exceeded", ge, YouTubeErrorCode.QUOTA_POINTS_EXCEEDED);
    } else {
      return new IllegalStateException(errorMessage, ge);
    }
  }

}
