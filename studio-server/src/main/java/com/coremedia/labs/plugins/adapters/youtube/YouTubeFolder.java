package com.coremedia.labs.plugins.adapters.youtube;


import com.coremedia.contenthub.api.ContentHubBlob;
import com.coremedia.contenthub.api.ContentHubObjectId;
import com.coremedia.contenthub.api.ContentHubType;
import com.coremedia.contenthub.api.Folder;
import com.coremedia.contenthub.api.UrlBlobBuilder;
import com.coremedia.contenthub.api.preview.DetailsElement;
import com.coremedia.contenthub.api.preview.DetailsSection;
import com.google.api.services.youtube.model.Playlist;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class YouTubeFolder extends YouTubeHubObject implements Folder {
  private final ContentHubType type;

  private final String name;
  private final Playlist playlist;

  /**
   * Constructor for the channel
   */
  YouTubeFolder(ContentHubObjectId id, String name) {
    super(id);
    this.playlist = null;
    this.name = name;
    this.type = new ContentHubType(YouTubeTypes.CHANNEL);
  }

  /**
   * Constructor for playlists
   */
  YouTubeFolder(ContentHubObjectId id, Playlist playlist) {
    super(id);
    this.playlist = playlist;
    this.name = playlist.getSnippet().getTitle();
    this.type = new ContentHubType(YouTubeTypes.PLAYLIST);
  }

  @NonNull
  @Override
  public List<DetailsSection> getDetails() {
    if(playlist == null){
      return super.getDetails();
    }

    List<DetailsElement<?>> detailElements;
    //noinspection ConstantConditions => null value is checked
    if(formatPreviewString(playlist.getSnippet().getDescription()) != null && !formatPreviewString(playlist.getSnippet().getDescription()).isEmpty()){
      detailElements = List.of(
              new DetailsElement<>("text", formatPreviewString(playlist.getSnippet().getDescription())),
              new DetailsElement<>("lastModified", formatPreviewDate(playlist.getSnippet().getPublishedAt())),
              new DetailsElement<>("channelId", playlist.getSnippet().getChannelId())
      );
    } else {
      detailElements = List.of(
              new DetailsElement<>("lastModified", formatPreviewDate(playlist.getSnippet().getPublishedAt())),
              new DetailsElement<>("channelId", playlist.getSnippet().getChannelId()));
    }
    return List.of(new DetailsSection("main", List.of(
            new DetailsElement<>(getName(), false, getPlayListPicture())
            ), false, false, false),
            new DetailsSection("metadata", detailElements.stream().filter(p -> Objects.nonNull(p.getValue())).collect(Collectors.toUnmodifiableList())));
  }

  @Nullable
  @Override
  public ContentHubBlob getBlob(String classifier) {
    return new UrlBlobBuilder(this, classifier).withUrl(getThumbnailUrl()).build();
  }

  private Object getPlayListPicture() {
    return Objects.requireNonNullElse(
            new UrlBlobBuilder(this, "playlist").withUrl(getThumbnailUrl()).build(),
            SHOW_TYPE_ICON);
  }

  private String getThumbnailUrl() {
    return getPlaylist().getSnippet().getThumbnails().getDefault().getUrl();
  }

  Playlist getPlaylist() {
    return playlist;
  }


  @NonNull
  @Override
  public String getName() {
    return name;
  }

  @NonNull
  @Override
  public ContentHubType getContentHubType() {
    return type;
  }
}
