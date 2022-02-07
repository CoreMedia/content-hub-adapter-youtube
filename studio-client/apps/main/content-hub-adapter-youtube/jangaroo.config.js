const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  sencha: {
    name: "com.coremedia.labs.plugins__studio-client.content-hub-adapter-youtube",
    namespace: "com.coremedia.labs.plugins.adapters.youtube",
    studioPlugins: [
      {
        mainClass: "com.coremedia.labs.plugins.adapters.youtube.ContentHubStudioYoutubePlugin",
        name: "Content Hub YouTube",
      },
    ],
  },
});
