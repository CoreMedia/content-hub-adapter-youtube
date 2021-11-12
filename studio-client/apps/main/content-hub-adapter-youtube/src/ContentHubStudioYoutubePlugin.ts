import ContentHub_properties from "@coremedia/studio-client.main.content-hub-editor-components/ContentHub_properties";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import ContentHubYoutube_properties from "./ContentHubYoutube_properties";

interface ContentHubStudioYoutubePluginConfig extends Config<StudioPlugin> {
}

class ContentHubStudioYoutubePlugin extends StudioPlugin {
  declare Config: ContentHubStudioYoutubePluginConfig;

  static readonly xtype: string = "com.coremedia.labs.plugins.adapters.youtube.ContentHubStudioYoutubePlugin";

  constructor(config: Config<ContentHubStudioYoutubePlugin> = null) {
    super(ConfigUtils.apply(Config(ContentHubStudioYoutubePlugin, {

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentHub_properties),
          source: resourceManager.getResourceBundle(null, ContentHubYoutube_properties),
        }),
      ],

    }), config));
  }
}

export default ContentHubStudioYoutubePlugin;
