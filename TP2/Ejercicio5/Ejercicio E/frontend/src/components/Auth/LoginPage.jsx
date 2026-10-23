import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { MOCK_USERS } from '../../api/mockBackendService';
import { Utensils, Shield, ShieldCheck, Key, Lock, ArrowRight, UserCheck, Sparkles, CheckCircle2, AlertCircle, Zap, User } from 'lucide-react';

export const LoginPage = () => {
  const { login } = useAuth();
  const [activeTab, setActiveTab] = useState('quick'); // 'quick' | 'credentials'
  const [emailInput, setEmailInput] = useState('');
  const [passwordInput, setPasswordInput] = useState('');
  const [isAuthenticating, setIsAuthenticating] = useState(false);
  const [selectedUserForLoading, setSelectedUserForLoading] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');

  const handleQuickLogin = (user) => {
    setSelectedUserForLoading(user);
    setIsAuthenticating(true);
    setErrorMessage('');
    setTimeout(() => {
      login(user);
      setIsAuthenticating(false);
    }, 400);
  };

  const handleCredentialsSubmit = (e) => {
    e.preventDefault();
    setIsAuthenticating(true);
    setErrorMessage('');

    const trimmedEmail = emailInput.trim().toLowerCase();
    const matchedUser = MOCK_USERS.find(u => u.email.toLowerCase() === trimmedEmail);

    // 1. Validar existencia del usuario en la base de identidades
    if (!matchedUser) {
      setTimeout(() => {
        setErrorMessage('401 Unauthorized: El usuario no existe en la base de identidades de Auth0.');
        setIsAuthenticating(false);
      }, 450);
      return;
    }

    // 2. Validar contraseña estricta
    const isPasswordValid = passwordInput === matchedUser.password || passwordInput === 'fudo2026';

    if (!isPasswordValid) {
      setTimeout(() => {
        setErrorMessage('401 Unauthorized: Contraseña incorrecta. Auth0 denegó la autenticación.');
        setIsAuthenticating(false);
      }, 450);
      return;
    }

    // Credenciales válidas: emitir token
    setTimeout(() => {
      login(matchedUser);
      setIsAuthenticating(false);
    }, 450);
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '24px',
      background: 'radial-gradient(ellipse at 50% 20%, rgba(16, 185, 129, 0.08) 0%, rgba(9, 13, 22, 0.98) 70%)'
    }}>
      <div className="glass-panel" style={{ width: '100%', maxWidth: '580px', padding: '36px', position: 'relative', overflow: 'hidden' }}>
        {/* Glow effect */}
        <div style={{ position: 'absolute', top: '-60px', right: '-60px', width: '150px', height: '150px', background: 'rgba(16, 185, 129, 0.2)', borderRadius: '50%', filter: 'blur(50px)', pointerEvents: 'none' }} />

        {/* Auth0 Universal Login Header */}
        <div style={{ textAlign: 'center', marginBottom: '28px' }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '6px', padding: '4px 12px', borderRadius: '20px', background: 'rgba(139, 92, 246, 0.12)', border: '1px solid rgba(139, 92, 246, 0.3)', marginBottom: '14px', fontSize: '0.75rem', color: '#c084fc', fontWeight: 700 }}>
            <ShieldCheck size={14} /> Universal Login Delegado • Auth0 (OAuth 2.0 / OIDC)
          </div>

          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '10px', marginBottom: '8px' }}>
            <div className="brand-icon-box" style={{ width: '42px', height: '42px' }}>
              <Utensils size={22} />
            </div>
            <h1 style={{ fontSize: '1.65rem', fontWeight: 800, color: 'var(--text-main)', letterSpacing: '-0.02em' }}>
              FUDO <span style={{ color: '#10b981' }}>RESTO</span>
            </h1>
          </div>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
            Plataforma Gastronómica de Gestión de Turnos y Mesas
          </p>
        </div>

        {/* Tab Switcher: Quick Demo vs Credentials */}
        <div style={{ display: 'flex', background: 'var(--bg-dark-900)', padding: '4px', borderRadius: 'var(--radius-md)', border: '1px solid var(--glass-border)', marginBottom: '24px' }}>
          <button
            type="button"
            className={`salon-tab-btn ${activeTab === 'quick' ? 'active' : ''}`}
            onClick={() => setActiveTab('quick')}
            style={{ flex: 1, textAlign: 'center', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '6px' }}
          >
            <Zap size={14} /> Acceso Rápido por Rol (Demo TP2)
          </button>
          <button
            type="button"
            className={`salon-tab-btn ${activeTab === 'credentials' ? 'active' : ''}`}
            onClick={() => setActiveTab('credentials')}
            style={{ flex: 1, textAlign: 'center', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '6px' }}
          >
            <Key size={14} /> Credenciales (Email & Password)
          </button>
        </div>

        {/* TAB 1: QUICK ROLE CARDS (Designed for professor / evaluator inspection) */}
        {activeTab === 'quick' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            <div style={{ fontSize: '0.775rem', color: 'var(--text-muted)', marginBottom: '4px' }}>
              Selecciona una identidad simulada de Auth0 para ingresar de inmediato:
            </div>

            {MOCK_USERS.map(user => {
              const isSelected = selectedUserForLoading?.auth0Id === user.auth0Id;

              return (
                <div
                  key={user.auth0Id}
                  onClick={() => !isAuthenticating && handleQuickLogin(user)}
                  style={{
                    background: isSelected ? 'rgba(16, 185, 129, 0.15)' : 'var(--bg-dark-750)',
                    border: `1px solid ${isSelected ? '#10b981' : 'var(--glass-border)'}`,
                    borderRadius: 'var(--radius-md)',
                    padding: '12px 16px',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    cursor: isAuthenticating ? 'not-allowed' : 'pointer',
                    transition: 'all 0.15s ease'
                  }}
                  className="role-card-hover"
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                    <div style={{ width: '38px', height: '38px', borderRadius: '50%', background: 'var(--bg-dark-900)', border: '1px solid var(--glass-border)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <User size={18} color="var(--text-muted)" />
                    </div>
                    <div>
                      <div style={{ fontWeight: 700, fontSize: '0.9rem', color: 'var(--text-main)', display: 'flex', alignItems: 'center', gap: '6px' }}>
                        {user.nombre}
                        <span className={`badge-${user.rol.toLowerCase().replace('_', '-')}`} style={{ fontSize: '0.65rem', padding: '1px 6px', borderRadius: '4px' }}>
                          {user.rol.replace('ROLE_', '')}
                        </span>
                      </div>
                      <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>
                        {user.email}
                      </div>
                    </div>
                  </div>

                  <div style={{ color: isSelected ? '#10b981' : 'var(--text-dim)' }}>
                    {isSelected && isAuthenticating ? (
                      <span style={{ fontSize: '0.75rem', fontWeight: 700, color: '#10b981' }}>Firmando JWT...</span>
                    ) : (
                      <ArrowRight size={16} />
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}

        {/* TAB 2: CREDENTIALS & SOCIAL LOGIN */}
        {activeTab === 'credentials' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            {/* Social Logins via Auth0 Broker */}
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              <button
                type="button"
                onClick={() => handleQuickLogin(MOCK_USERS[1])} // Mateo Peralta vía Google
                className="btn btn-secondary"
                disabled={isAuthenticating}
                style={{
                  width: '100%',
                  padding: '11px',
                  background: 'rgba(255, 255, 255, 0.05)',
                  borderColor: 'rgba(255, 255, 255, 0.15)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '10px'
                }}
              >
                <svg width="18" height="18" viewBox="0 0 24 24">
                  <path fill="#EA4335" d="M12 5c1.6 0 3 .6 4.1 1.6l3.1-3.1C17.3 1.7 14.8 1 12 1 7.5 1 3.7 3.6 1.9 7.3l3.7 2.9C6.5 7.3 9 5 12 5z"/>
                  <path fill="#4285F4" d="M23.5 12.3c0-.8-.1-1.6-.2-2.3H12v4.5h6.5c-.3 1.5-1.1 2.8-2.4 3.7l3.7 2.9c2.2-2 3.7-5 3.7-8.8z"/>
                  <path fill="#FBBC05" d="M5.6 14.8c-.2-.7-.4-1.5-.4-2.3s.2-1.5.4-2.3L1.9 7.3C.7 9.7 0 12 0 14.5s.7 4.8 1.9 7.2l3.7-2.9z"/>
                  <path fill="#34A853" d="M12 23.5c3.2 0 6-1.1 8-3l-3.7-2.9c-1.1.7-2.5 1.2-4.3 1.2-3 0-5.5-2.3-6.4-5.2L1.9 16.5C3.7 20.3 7.5 23.5 12 23.5z"/>
                </svg>
                <span>Continuar con Google</span>
              </button>

              <button
                type="button"
                onClick={() => handleQuickLogin(MOCK_USERS[4])} // Admin vía GitHub
                className="btn btn-secondary"
                disabled={isAuthenticating}
                style={{
                  width: '100%',
                  padding: '11px',
                  background: 'rgba(255, 255, 255, 0.05)',
                  borderColor: 'rgba(255, 255, 255, 0.15)',
                  display: 'flex',
                  alignItems: 'center',
                  justifyContent: 'center',
                  gap: '10px'
                }}
              >
                <svg width="18" height="18" fill="white" viewBox="0 0 24 24">
                  <path d="M12 0C5.37 0 0 5.37 0 12c0 5.31 3.435 9.795 8.205 11.385.6.105.825-.255.825-.57 0-.285-.015-1.23-.015-2.235-3.015.555-3.795-.735-4.035-1.41-.135-.345-.72-1.41-1.23-1.695-.42-.225-1.02-.78-.015-.795.945-.015 1.62.87 1.845 1.23 1.08 1.815 2.805 1.305 3.495.99.105-.78.42-1.305.765-1.605-2.67-.3-5.46-1.335-5.46-5.925 0-1.305.465-2.385 1.23-3.225-.12-.3-.54-1.53.12-3.18 0 0 1.005-.315 3.3 1.23.96-.27 1.98-.405 3-.405s2.04.135 3 .405c2.295-1.56 3.3-1.23 3.3-1.23.66 1.65.24 2.88.12 3.18.765.84 1.23 1.905 1.23 3.225 0 4.605-2.805 5.625-5.475 5.925.435.375.81 1.095.81 2.22 0 1.605-.015 2.895-.015 3.3 0 .315.225.69.825.57A12.02 12.02 0 0024 12c0-6.63-5.37-12-12-12z"/>
                </svg>
                <span>Continuar con GitHub</span>
              </button>
            </div>

            {/* Divider */}
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px', margin: '4px 0' }}>
              <div style={{ flex: 1, height: '1px', background: 'var(--glass-border)' }} />
              <span style={{ fontSize: '0.725rem', color: 'var(--text-dim)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                o con correo electrónico
              </span>
              <div style={{ flex: 1, height: '1px', background: 'var(--glass-border)' }} />
            </div>

            <form onSubmit={handleCredentialsSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
              {/* Error Alert Banner */}
              {errorMessage && (
                <div style={{
                  background: 'rgba(239, 68, 68, 0.12)',
                  border: '1px solid rgba(239, 68, 68, 0.4)',
                  borderRadius: 'var(--radius-md)',
                  padding: '10px 14px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '10px',
                  color: '#f87171',
                  fontSize: '0.8rem',
                  animation: 'fadeIn 0.2s ease-out'
                }}>
                  <AlertCircle size={18} style={{ flexShrink: 0 }} />
                  <span>{errorMessage}</span>
                </div>
              )}

              <div className="wizard-form-group" style={{ marginBottom: 0 }}>
                <label className="wizard-label">Correo Electrónico</label>
                <input
                  type="email"
                  required
                  placeholder="ejemplo@fudo.com o admin@fudo-resto.com"
                  className="wizard-input"
                  value={emailInput}
                  onChange={e => {
                    setEmailInput(e.target.value);
                    if (errorMessage) setErrorMessage('');
                  }}
                  autoFocus
                />
              </div>

              <div className="wizard-form-group" style={{ marginBottom: 0 }}>
                <label className="wizard-label">Contraseña</label>
                <input
                  type="password"
                  required
                  placeholder="••••••••••••"
                  className="wizard-input"
                  value={passwordInput}
                  onChange={e => {
                    setPasswordInput(e.target.value);
                    if (errorMessage) setErrorMessage('');
                  }}
                />
              </div>

              <button
                type="submit"
                disabled={isAuthenticating}
                className="btn btn-primary"
                style={{ width: '100%', padding: '12px', marginTop: '4px' }}
              >
                {isAuthenticating ? 'Validando con Auth0...' : 'Iniciar Sesión con Auth0'}
              </button>

              {/* Credential Guide Box */}
              <div style={{
                background: 'rgba(15, 23, 42, 0.7)',
                border: '1px dashed var(--glass-border)',
                borderRadius: 'var(--radius-md)',
                padding: '10px 12px',
                fontSize: '0.725rem',
                color: 'var(--text-muted)',
                lineHeight: 1.5
              }}>
                <div style={{ fontWeight: 700, color: '#38bdf8', marginBottom: '2px', display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <Key size={13} color="#38bdf8" /> Cuentas y Contraseñas Registradas:
                </div>
                <div>• <strong>Admin:</strong> <code>admin@fudo-resto.com</code> (Clave: <code>Admin123!</code>)</div>
                <div>• <strong>Recepción:</strong> <code>valentina.host@fudo.com</code> (Clave: <code>Host123!</code>)</div>
                <div>• <strong>Mozo:</strong> <code>carlos.mozo@fudo.com</code> (Clave: <code>Mozo123!</code>)</div>
                <div>• <strong>Comensal:</strong> <code>mateo.comensal@email.com</code> (Clave: <code>Mateo123!</code>)</div>
                <div style={{ color: '#fbbf24', marginTop: '2px' }}>• Clave Maestra de Testing: <code>fudo2026</code></div>
              </div>
            </form>
          </div>
        )}

        {/* Security Architecture Guarantees */}
        <div style={{ marginTop: '24px', paddingTop: '18px', borderTop: '1px solid var(--glass-border)', display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', fontSize: '0.725rem', color: 'var(--text-dim)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <Lock size={13} color="#10b981" />
            <span>Firma asimétrica RS256</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <Shield size={13} color="#38bdf8" />
            <span>Backend 100% Stateless</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <Sparkles size={13} color="#c084fc" />
            <span>Protección BOLA @CheckOwnership</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
            <CheckCircle2 size={13} color="#fbbf24" />
            <span>Rate Limiter Token Bucket</span>
          </div>
        </div>
      </div>
    </div>
  );
};
