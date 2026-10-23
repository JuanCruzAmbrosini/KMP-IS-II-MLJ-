import React, { createContext, useContext, useState, useEffect } from 'react';
import { MOCK_USERS, MOCK_AUTH0_ISSUER, MOCK_AUTH0_AUDIENCE } from '../api/mockBackendService';
import { apiClient } from '../api/apiClient';
import { Roles } from '../api/types';

const AuthContext = createContext(null);

const STORAGE_KEY_AUTH = 'fudo_auth_state_v1';

export const AuthProvider = ({ children }) => {
  // Estado de autenticación
  const [currentUser, setCurrentUser] = useState(() => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY_AUTH);
      if (saved) {
        const parsed = JSON.parse(saved);
        return parsed.user || MOCK_USERS[0];
      }
    } catch {}
    return MOCK_USERS[0];
  });

  const [isAuthenticated, setIsAuthenticated] = useState(() => {
    try {
      const saved = localStorage.getItem(STORAGE_KEY_AUTH);
      if (saved) {
        const parsed = JSON.parse(saved);
        return parsed.isAuthenticated ?? true;
      }
    } catch {}
    // Por defecto iniciamos con sesión para comodidad, pero con botón visible de logout
    return true;
  });

  const [jwtToken, setJwtToken] = useState('');
  const [decodedClaims, setDecodedClaims] = useState(null);
  const [showTokenModal, setShowTokenModal] = useState(false);

  // Genera un token JWT estructurado como el de Auth0 RS256
  const generateSimulatedJwt = (user) => {
    const header = {
      alg: 'RS256',
      typ: 'JWT',
      kid: 'fudo-auth0-key-2026-rs256'
    };

    const now = Math.floor(Date.now() / 1000);
    const payload = {
      iss: MOCK_AUTH0_ISSUER,
      sub: user.auth0Id,
      aud: [MOCK_AUTH0_AUDIENCE, `${MOCK_AUTH0_ISSUER}userinfo`],
      iat: now,
      exp: now + 3600 * 2, // 2 horas
      azp: 'fudo_web_client_frontend',
      scope: 'openid profile email read:mesas write:mesas read:reservas write:reservas',
      email: user.email,
      name: user.nombre,
      'https://api.fudo-resto.local/roles': [user.rol],
      'https://api.fudo-resto.local/permissions': getPermissionsForRole(user.rol)
    };

    const encodeBase64Url = (obj) => {
      const json = JSON.stringify(obj);
      return btoa(unescape(encodeURIComponent(json)))
        .replace(/=/g, '')
        .replace(/\+/g, '-')
        .replace(/\//g, '_');
    };

    const encodedHeader = encodeBase64Url(header);
    const encodedPayload = encodeBase64Url(payload);
    // Firma simulada criptográfica de 256 bits
    const signature = 'G0B_5r_z_9X9sWq_SignatureRS256_Auth0Verified_fudoGastronomicoSecureHash';

    return {
      rawToken: `${encodedHeader}.${encodedPayload}.${signature}`,
      payload
    };
  };

  const applyAuth = (user, authenticated = true) => {
    setCurrentUser(user);
    setIsAuthenticated(authenticated);
    if (authenticated && user) {
      const { rawToken, payload } = generateSimulatedJwt(user);
      setJwtToken(rawToken);
      setDecodedClaims(payload);
      apiClient.setAuthToken(rawToken, user);
      localStorage.setItem(STORAGE_KEY_AUTH, JSON.stringify({ user, isAuthenticated: true }));
    } else {
      setJwtToken('');
      setDecodedClaims(null);
      apiClient.setAuthToken(null, null);
      localStorage.setItem(STORAGE_KEY_AUTH, JSON.stringify({ user: null, isAuthenticated: false }));
    }
  };

  const login = (user) => {
    applyAuth(user, true);
  };

  const logout = () => {
    applyAuth(null, false);
  };

  const switchUser = (user) => {
    applyAuth(user, true);
  };

  useEffect(() => {
    if (isAuthenticated && currentUser) {
      applyAuth(currentUser, true);
    }
  }, []);

  const hasRole = (role) => currentUser?.rol === role;

  return (
    <AuthContext.Provider
      value={{
        currentUser,
        isAuthenticated,
        login,
        logout,
        jwtToken,
        decodedClaims,
        switchUser,
        availableUsers: MOCK_USERS,
        hasRole,
        showTokenModal,
        setShowTokenModal,
        isAdmin: currentUser?.rol === Roles.ROLE_ADMIN,
        isRecepcion: currentUser?.rol === Roles.ROLE_RECEPCION,
        isMozo: currentUser?.rol === Roles.ROLE_MOZO,
        isCliente: currentUser?.rol === Roles.ROLE_CLIENTE
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

const getPermissionsForRole = (rol) => {
  switch (rol) {
    case Roles.ROLE_ADMIN:
      return ['admin:all', 'salones:write', 'mesas:write', 'turnos:write', 'audit:read'];
    case Roles.ROLE_RECEPCION:
      return ['mesas:read', 'mesas:state:patch', 'reservas:checkin', 'reservas:noshow', 'reservas:walkin'];
    case Roles.ROLE_MOZO:
      return ['mesas:read', 'mesas:state:patch', 'reservas:read'];
    case Roles.ROLE_CLIENTE:
      return ['reservas:create', 'reservas:read:own', 'reservas:cancel:own'];
    default:
      return ['read:public'];
  }
};

export const useAuth = () => useContext(AuthContext);
