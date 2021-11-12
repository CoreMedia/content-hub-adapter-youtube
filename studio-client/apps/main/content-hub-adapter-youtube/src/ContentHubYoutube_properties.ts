import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";

/**
 * Interface values for ResourceBundle "ContentHubYoutube".
 * @see ContentHubYoutube_properties
 */
interface ContentHubYoutube_properties {

/**
 *Youtube
 */
  item_type_youtube_name: string;
  item_type_youtube_icon: string;
  folder_type_playlist_name: string;
  folder_type_playlist_icon: string;
  folder_type_youtubechannel_name: string;
  folder_type_youtubechannel_icon: string;
  adapter_type_youtube_name: string;
  adapter_type_youtube_icon: string;
  metadata_sectionName: string;
  text_sectionItemKey: string;
  lastModified_sectionItemKey: string;
  videoId_sectionItemKey: string;
  channelId_sectionItemKey: string;
  link_sectionItemKey: string;
  YouTubeErrorCode_USAGE_LIMIT_EXCEEDED_title: string;
  YouTubeErrorCode_USAGE_LIMIT_EXCEEDED: string;
  YouTubeErrorCode_QUOTA_POINTS_EXCEEDED_title: string;
  YouTubeErrorCode_QUOTA_POINTS_EXCEEDED: string;
}

/**
 * Singleton for the current user Locale's instance of ResourceBundle "ContentHubYoutube".
 * @see ContentHubYoutube_properties
 */
const ContentHubYoutube_properties: ContentHubYoutube_properties = {
  item_type_youtube_name: "Video",
  item_type_youtube_icon: CoreIcons_properties.youtube,
  folder_type_playlist_name: "Playlist",
  folder_type_playlist_icon: CoreIcons_properties.bulleted_list,
  folder_type_youtubechannel_name: "Channel",
  folder_type_youtubechannel_icon: CoreIcons_properties.youtube_channel_youtube_video,
  adapter_type_youtube_name: "YouTube",
  adapter_type_youtube_icon: CoreIcons_properties.youtube_channel_youtube_video,
  metadata_sectionName: "Metadata",
  text_sectionItemKey: "Description",
  lastModified_sectionItemKey: "Last modified",
  videoId_sectionItemKey: "ID",
  channelId_sectionItemKey: "ID",
  link_sectionItemKey: "Link",
  YouTubeErrorCode_USAGE_LIMIT_EXCEEDED_title: "The daily limit was exceeded",
  YouTubeErrorCode_USAGE_LIMIT_EXCEEDED: "Response from Youtube: '{1}'",
  YouTubeErrorCode_QUOTA_POINTS_EXCEEDED_title: "Quota Exceeded",
  YouTubeErrorCode_QUOTA_POINTS_EXCEEDED: "The request cannot be completed because you have exceeded your quota within your Google account.",
};

export default ContentHubYoutube_properties;
