exports.onExecutePostUserRegistration = async (event, api) => {
  const domain = event.secrets.AUTH0_DOMAIN;
  const managementApiAudience = `https://${domain}/api/v2/`;

  const tokenResponse = await fetch(`https://${domain}/oauth/token`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      client_id: event.secrets.M2M_CLIENT_ID,
      client_secret: event.secrets.M2M_CLIENT_SECRET,
      audience: managementApiAudience,
      grant_type: "client_credentials",
    }),
  });

  if (!tokenResponse.ok) {
    throw new Error(`Could not obtain Auth0 Management API token: ${tokenResponse.status}`);
  }

  const { access_token: accessToken } = await tokenResponse.json();
  const roleResponse = await fetch(
    `https://${domain}/api/v2/users/${encodeURIComponent(event.user.user_id)}/roles`,
    {
      method: "POST",
      headers: {
        Authorization: `Bearer ${accessToken}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        roles: [event.secrets.CLIENT_ROLE_ID],
      }),
    }
  );

  if (!roleResponse.ok) {
    throw new Error(`Could not assign client role: ${roleResponse.status}`);
  }
};
