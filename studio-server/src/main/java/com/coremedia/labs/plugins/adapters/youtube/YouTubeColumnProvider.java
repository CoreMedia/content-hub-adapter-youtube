package com.coremedia.labs.plugins.adapters.youtube;

import com.coremedia.contenthub.api.ContentHubObject;
import com.coremedia.contenthub.api.Folder;
import com.coremedia.contenthub.api.column.Column;
import com.coremedia.contenthub.api.column.ColumnValue;
import com.coremedia.contenthub.api.column.DefaultColumnProvider;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Playlist;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Adds a custom column for the lifecycle status of a content.
 */
public class YouTubeColumnProvider extends DefaultColumnProvider {

  public static final String DATA_INDEX_NAME_COLUMN = "name";
  public static final String DATA_INDEX_LAST_MODIFIED = "lastModified";

  @NonNull
  @Override
  public List<Column> getColumns(Folder folder) {
    List<Column> columns = new ArrayList<>(super.getColumns(folder));
    columns.add(new Column("lastModified", DATA_INDEX_LAST_MODIFIED, 100, -1));
    return columns;
  }

  @NonNull
  @Override
  public List<ColumnValue> getColumnValues(ContentHubObject hubObject) {
    List<ColumnValue> columnValues = new ArrayList<>(super.getColumnValues(hubObject));

    DateTime lastModified = null;
    if (hubObject instanceof YouTubeItem) {
      YouTubeItem youTubeItem = (YouTubeItem) hubObject;
      lastModified = youTubeItem.getLastModified();
    } else if (hubObject instanceof YouTubeFolder) {
      YouTubeFolder youtubeFolder = (YouTubeFolder) hubObject;
      Playlist playlist = youtubeFolder.getPlaylist();
      lastModified = playlist != null ? playlist.getSnippet().getPublishedAt() : null;
    }
    columnValues.add(new ColumnValue(DATA_INDEX_LAST_MODIFIED, getLastModified(lastModified), null, null));

    return columnValues;
  }

  private Calendar getLastModified(@Nullable DateTime dateTime) {
    if (dateTime != null) {
      Calendar instance = Calendar.getInstance();
      instance.setTimeInMillis(dateTime.getValue());
      return instance;
    }
    return null;
  }
}
