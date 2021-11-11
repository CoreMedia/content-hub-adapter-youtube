/** @type { import('@jangaroo/core').IJangarooConfig } */
module.exports = {
  type: "code",
  extName: "com.coremedia.labs.plugins__studio-client.content-hub-adapter-youtube",
  extNamespace: "com.coremedia.labs.plugins.adapters.youtube",
  sencha: {
    studioPlugins: [
      {
        mainClass: "com.coremedia.labs.plugins.adapters.youtube.ContentHubStudioyoutubePlugin",
        name: "Content Hub",
      },
    ],
  },
  command: {
    build: {
      ignoreTypeErrors: true
    },
  },
};
