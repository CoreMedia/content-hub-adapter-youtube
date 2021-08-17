package com.coremedia.labs.plugins.adapters.youtube;

import com.coremedia.contenthub.api.ContentCreationUtil;
import com.coremedia.contenthub.api.ContentHubAdapter;
import com.coremedia.contenthub.api.ContentHubContext;
import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.ContentHubTransformer;
import com.coremedia.contenthub.api.ContentModel;
import com.coremedia.contenthub.api.ContentModelReference;
import com.coremedia.contenthub.api.Item;
import com.coremedia.contenthub.api.UrlBlobBuilder;
import com.google.api.services.youtube.model.ThumbnailDetails;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

class YouTubeContentHubTransformer implements ContentHubTransformer {
  @NonNull
  @Override
  public ContentModel transform(Item item, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) {
    if (!(item instanceof YouTubeItem)) {
      throw new IllegalArgumentException(item + " is not a YouTubeItem.  Cannot handle.");
    }
    return transformYouTubeItem((YouTubeItem)item);
  }

  @Override
  @Nullable
  public ContentModel resolveReference(ContentHubObject owner, ContentModelReference reference, ContentHubAdapter contentHubAdapter, ContentHubContext contentHubContext) {
    Object data = reference.getData();
    if (!(data instanceof String)) {
      throw new IllegalArgumentException("Not my reference: " + reference);
    }

    String imageUrl = (String) data;
    String imageName = reference.getOwner().getContentName() + " (Thumbnail)";

    ContentModel referenceModel = ContentModel.createReferenceModel(imageName, reference.getCoreMediaContentType());

    referenceModel.put("data", new UrlBlobBuilder(owner, "thumbnail").withUrl(imageUrl).build());
    referenceModel.put("title", "YouTube Thumbnail " + imageName);

    return referenceModel;
  }


  // --- internal ---------------------------------------------------

  private ContentModel transformYouTubeItem(YouTubeItem item) {
    ContentModel contentModel = ContentModel.createContentModel(item);
    contentModel.put("title", item.getName());

    //put URL
    contentModel.put("dataUrl", item.getVideoUrl());

    //put description
    String description = item.getDescription();
    if (!StringUtils.isEmpty(description)) {
      contentModel.put("detailText", ContentCreationUtil.convertStringToRichtext(description));
    }

    //store image references
    String url = thumbnailUrl(item.getThumbnails());
    if (url != null) {
      ContentModelReference ref = ContentModelReference.create(contentModel, "CMPicture", url);
      contentModel.put("pictures", Collections.singletonList(ref));
    }
    return contentModel;
  }

  @Nullable
  private static String thumbnailUrl(@Nullable ThumbnailDetails thumbnails) {
    String url = null;
    if (thumbnails != null) {
      if (thumbnails.getMaxres() != null) {
        url = thumbnails.getMaxres().getUrl();
      }
      if (url == null && thumbnails.getHigh() != null) {
        url = thumbnails.getHigh().getUrl();
      }
      if (url == null && thumbnails.getDefault() != null) {
        url = thumbnails.getDefault().getUrl();
      }
    }
    return url;
  }
}
