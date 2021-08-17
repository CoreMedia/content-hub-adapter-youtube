package com.coremedia.labs.plugins.adapters.youtube;


import com.coremedia.contenthub.api.ContentHubBlob;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.UrlBlobBuilder;
import com.coremedia.contenthub.api.preview.DetailsElement;
import com.coremedia.contenthub.api.preview.DetailsSection;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemSnippet;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoSnippet;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.util.HtmlUtils.htmlUnescape;

class YouTubeItem extends YouTubeHubObject implements Item {

  private final String name;
  private final String description;
  private final DateTime lastModified;
  private final ThumbnailDetails thumbnails;
  private final String videoId;


  // The constructors look like boilerplate code, but the YouTube classes are
  // indeed completely distinct.  :-(

  YouTubeItem(@NonNull ContentHubObjectId id, @NonNull Video video) {
    super(id);
    VideoSnippet snippet = video.getSnippet();
    if (snippet == null) {
      throw new IllegalArgumentException("Video " + video + " has no snippet.  Cannot handle.");
    }
    name = titleToName(snippet.getTitle());
    description = snippet.getDescription();
    lastModified = snippet.getPublishedAt();
    thumbnails = snippet.getThumbnails();
    videoId = video.getId();
  }

  YouTubeItem(@NonNull ContentHubObjectId id, @NonNull PlaylistItem item) {
    super(id);
    PlaylistItemSnippet snippet = item.getSnippet();
    if (snippet == null) {
      throw new IllegalArgumentException("PlayListItem " + item + " has no snippet.  Cannot handle.");
    }
    name = titleToName(snippet.getTitle());
    description = snippet.getDescription();
    lastModified = snippet.getPublishedAt();
    thumbnails = snippet.getThumbnails();
    videoId = snippet.getResourceId().getVideoId();
  }

  YouTubeItem(@NonNull ContentHubObjectId id, @NonNull SearchResult searchResult) {
    super(id);
    SearchResultSnippet snippet = searchResult.getSnippet();
    if (snippet == null) {
      throw new IllegalArgumentException("PlayListItem " + searchResult + " has no snippet.  Cannot handle.");
    }
    name = htmlUnescape(titleToName(snippet.getTitle()));
    description = htmlUnescape(snippet.getDescription());
    lastModified = snippet.getPublishedAt();
    thumbnails = snippet.getThumbnails();
    videoId = searchResult.getId().getVideoId();
  }


  // --- Item -------------------------------------------------------

  @NonNull
  @Override
  public ContentHubType getContentHubType() {
    return new ContentHubType(YouTubeTypes.ITEM);
  }

  @NonNull
  @Override
  public String getCoreMediaContentType() {
    return "CMVideo";
  }

  @NonNull
  @Override
  public String getName() {
    return name;
  }

  @Nullable
  @Override
  public String getDescription() {
    return description;
  }

  @NonNull
  @Override
  public List<DetailsSection> getDetails() {
    ContentHubBlob blob = getBlob("classifier");
    return List.of(new DetailsSection("main", List.of(
            new DetailsElement<>(getName(), false, Objects.requireNonNullElse(blob, SHOW_TYPE_ICON))
            ), false, false, false),
            new DetailsSection("metadata", List.of(
                    new DetailsElement<>("text", formatPreviewString(description)),
                    new DetailsElement<>("lastModified", formatPreviewDate(lastModified)),
                    new DetailsElement<>("videoId", videoId),
                    new DetailsElement<>("link", getVideoUrl())
            ).stream().filter(p -> Objects.nonNull(p.getValue())).collect(Collectors.toUnmodifiableList())));
  }

  @Nullable
  @Override
  public ContentHubBlob getBlob(String classifier) {
    Optional<String> defaultThumbnailUrl = getDefaultThumbnailUrl();
    if (defaultThumbnailUrl.isEmpty()) {
      return null;
    }

    return new UrlBlobBuilder(this, classifier).withUrl(defaultThumbnailUrl.get()).build();
  }

  @NonNull
  private Optional<String> getDefaultThumbnailUrl() {
    ThumbnailDetails thumbnails = getThumbnails();
    if (thumbnails == null) {
      return Optional.empty();
    }

    Thumbnail defaultThumbnail = thumbnails.getDefault();
    if (defaultThumbnail == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(defaultThumbnail.getUrl());
  }

  // --- more features ----------------------------------------------

  DateTime getLastModified() {
    return lastModified;
  }

  ThumbnailDetails getThumbnails() {
    return thumbnails;
  }

  String getVideoUrl() {
    return "https://www.youtube.com/watch?v=" + videoId;
  }
}
