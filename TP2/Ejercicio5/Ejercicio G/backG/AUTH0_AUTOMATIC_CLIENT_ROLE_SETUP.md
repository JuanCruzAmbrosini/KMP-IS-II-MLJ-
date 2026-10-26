# Automatic client role assignment

The file `AUTH0_POST_USER_REGISTRATION_ACTION.js` assigns the `cliente` role to every new user created through a database or passwordless connection.

## 1. Create the role

In Auth0, go to **User Management -> Roles** and create:

```text
Name: cliente
```

Copy the role ID from the role details page. It looks like `rol_...`.

## 2. Create a Machine-to-Machine application

Go to **Applications -> APIs -> Auth0 Management API -> Machine to Machine Applications** and authorize a Machine-to-Machine application.

Grant only this scope:

```text
update:users
```

Do not use the SPA client ID from `FrontG/.env`.

## 3. Add Action secrets

Create a custom Action with trigger **Post User Registration** and add these secrets in the Action's **Secrets** panel:

```text
AUTH0_DOMAIN=dev-1x6qm5adx348o8vd.us.auth0.com
M2M_CLIENT_ID=<Machine-to-Machine client ID>
M2M_CLIENT_SECRET=<Machine-to-Machine client secret>
CLIENT_ROLE_ID=<ID of the cliente role>
```

Paste the code from `AUTH0_POST_USER_REGISTRATION_ACTION.js`, then **Deploy** it.

## 4. Connect the trigger

Go to **Actions -> Triggers -> post-user-registration**, add the Action to the flow, and press **Apply**.

The role assignment is asynchronous. A newly registered user may need to log in again before the role appears in the access token. The existing Post Login Action then adds the assigned roles to:

```text
https://example_yt/roles
```

Never commit the M2M client secret or put it in the frontend `.env`.
