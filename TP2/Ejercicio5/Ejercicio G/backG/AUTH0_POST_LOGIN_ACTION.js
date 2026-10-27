exports.onExecutePostLogin = async (event, api) => {
  const namespace = "https://example_yt";

  api.accessToken.setCustomClaim(
    `${namespace}/roles`,
    event.authorization?.roles || []
  );
};
