import ContentHubYoutube_properties from "./ContentHubYoutube_properties";
import ResourceBundleUtil from "@jangaroo/runtime/l10n/ResourceBundleUtil";

/**
 * Overrides of ResourceBundle "ContentHubYoutube" for Locale "de".
 * @see ContentHubYoutube_properties#INSTANCE
 */
ResourceBundleUtil.override(ContentHubYoutube_properties, {
 metadata_previewName: "Metadaten",
 text_sectionItemKey: "Beschreibung",
 lastModified_sectionItemKey: "Zuletzt bearbeitet",
 videoId_sectionItemKey: "ID",
 link_sectionItemKey: "Link",
 YouTubeErrorCode_USAGE_LIMIT_EXCEEDED_title: "Ihr Tageslimit an \"quota\" ist aufgebraucht",
 YouTubeErrorCode_USAGE_LIMIT_EXCEEDED: "Antwort von Youtube: '{1}'",
 YouTubeErrorCode_QUOTA_POINTS_EXCEEDED_title: "Ihr Tageslimit an \"quota\" ist aufgebraucht",
 YouTubeErrorCode_QUOTA_POINTS_EXCEEDED: "Die Anfrage kann nicht abgeschlossen werden, da Sie Ihr \"quota\" in Ihrem Google-Konto überschritten haben."
});
