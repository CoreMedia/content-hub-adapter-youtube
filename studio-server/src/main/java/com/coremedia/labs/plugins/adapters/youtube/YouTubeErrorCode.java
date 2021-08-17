package com.coremedia.labs.plugins.adapters.youtube;

import com.coremedia.contenthub.api.exception.ContentHubExceptionErrorCode;

public enum YouTubeErrorCode implements ContentHubExceptionErrorCode {
  QUOTA_POINTS_EXCEEDED, USAGE_LIMIT_EXCEEDED
}
