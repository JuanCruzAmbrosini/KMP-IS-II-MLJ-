import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { ShieldCheck, Key, Lock, Copy, Check, X, Code2, Server } from 'lucide-react';

export const Auth0TokenModal = () => {
  const { showTokenModal, setShowTokenModal, currentUser, jwtToken, decodedClaims } = useAuth();
  const [copied, setCopied] = useState(false);
  const [activeTab, setActiveTab] = useState('payload');

  if (!showTokenModal) return null;

  const handleCopy = () => {
    navigator.clipboard.writeText(jwtToken);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const tokenParts = jwtToken.split('.');
  const rawHeader = tokenParts[0] || '';
  const rawPayload = tokenParts[1] || '';
  const rawSignature = tokenParts[2] || '';

  return (
    <div className="modal-overlay" onClick={() => setShowTokenModal(false)}>
      <div className="modal-content" onClick={e => e.stopPropagation()} style={{ maxWidth: '720px' }}>
        <div style={{ padding: '20px 24px', borderBottom: '1px solid var(--glass-border)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{ width: '36px', height: '36px', borderRadius: '8px', background: 'rgba(139, 92, 246, 0.2)', border: '1px solid rgba(139, 92, 246, 0.4)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#c084fc' }}>
              <ShieldCheck size={20} />
            </div>
            <div>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--text-main)' }}>
                Inspector de Token Auth0 (JWT RS256)
              </h3>
              <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                Validación Stateless con Clave Pública Asimétrica (Sección 5.2 de PLAN.md)
              </p>
            </div>
          </div>
          <button
            onClick={() => setShowTokenModal(false)}
            style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}
          >
            <X size={20} />
          </button>
        </div>

        <div style={{ padding: '20px 24px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {/* User Active Summary */}
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '12px 16px', background: 'var(--bg-dark-900)', borderRadius: 'var(--radius-md)', border: '1px solid var(--glass-border)' }}>
            <div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Usuario Autenticado (sub):</div>
              <div style={{ fontSize: '0.9rem', fontWeight: 700, color: '#38bdf8', fontFamily: 'var(--font-mono)' }}>
                {currentUser.auth0Id} ({currentUser.nombre})
              </div>
            </div>
            <span className={`badge-${currentUser.rol.toLowerCase().replace('_', '-')}`} style={{ padding: '4px 10px', borderRadius: '6px', fontSize: '0.75rem', fontWeight: 700 }}>
              {currentUser.rol}
            </span>
          </div>

          {/* Raw Visual JWT Highlight */}
          <div>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '6px' }}>
              <span style={{ fontSize: '0.775rem', fontWeight: 600, color: 'var(--text-muted)' }}>
                Formato RFC 7519: <span style={{ color: '#ef4444' }}>Header</span>.<span style={{ color: '#a855f7' }}>Payload</span>.<span style={{ color: '#0ea5e9' }}>Signature</span>
              </span>
              <button onClick={handleCopy} className="btn btn-secondary btn-sm" style={{ gap: '4px' }}>
                {copied ? <Check size={14} color="#10b981" /> : <Copy size={14} />}
                {copied ? 'Copiado' : 'Copiar Token'}
              </button>
            </div>
            <div style={{ background: '#070b14', padding: '12px', borderRadius: 'var(--radius-md)', border: '1px solid var(--glass-border)', wordBreak: 'break-all', fontFamily: 'var(--font-mono)', fontSize: '0.725rem', lineHeight: '1.6', maxHeight: '110px', overflowY: 'auto' }}>
              <span style={{ color: '#f87171' }}>{rawHeader}</span>
              <span style={{ color: '#ffffff' }}>.</span>
              <span style={{ color: '#c084fc' }}>{rawPayload}</span>
              <span style={{ color: '#ffffff' }}>.</span>
              <span style={{ color: '#38bdf8' }}>{rawSignature}</span>
            </div>
          </div>

          {/* Claims Decoded View */}
          <div>
            <div style={{ display: 'flex', gap: '8px', borderBottom: '1px solid var(--glass-border)', paddingBottom: '8px' }}>
              <button
                className={`btn btn-sm ${activeTab === 'payload' ? 'btn-primary' : 'btn-secondary'}`}
                onClick={() => setActiveTab('payload')}
              >
                Payload Claims
              </button>
              <button
                className={`btn btn-sm ${activeTab === 'security' ? 'btn-primary' : 'btn-secondary'}`}
                onClick={() => setActiveTab('security')}
              >
                Arquitectura de Seguridad Backend
              </button>
            </div>

            {activeTab === 'payload' && (
              <div style={{ marginTop: '12px', background: '#070b14', padding: '14px', borderRadius: 'var(--radius-md)', border: '1px solid var(--glass-border)', maxHeight: '220px', overflowY: 'auto' }}>
                <pre style={{ fontFamily: 'var(--font-mono)', fontSize: '0.75rem', color: '#a7f3d0' }}>
                  {JSON.stringify(decodedClaims, null, 2)}
                </pre>
              </div>
            )}

            {activeTab === 'security' && (
              <div style={{ marginTop: '12px', background: 'rgba(30, 41, 59, 0.5)', padding: '16px', borderRadius: 'var(--radius-md)', border: '1px solid var(--glass-border)', fontSize: '0.825rem', color: 'var(--text-muted)', display: 'flex', flexDirection: 'column', gap: '10px' }}>
                <div style={{ display: 'flex', gap: '8px', alignItems: 'flex-start' }}>
                  <Server size={18} color="#38bdf8" style={{ flexShrink: 0, marginTop: '2px' }} />
                  <div>
                    <strong style={{ color: 'var(--text-main)' }}>Backend 100% Stateless:</strong> Spring Boot no guarda sesiones HTTP. Cada request entrante valida la firma criptográfica usando la clave pública obtenida de Auth0 (JWKS).
                  </div>
                </div>
                <div style={{ display: 'flex', gap: '8px', alignItems: 'flex-start' }}>
                  <Lock size={18} color="#10b981" style={{ flexShrink: 0, marginTop: '2px' }} />
                  <div>
                    <strong style={{ color: 'var(--text-main)' }}>Protección BOLA / IDOR con Reflexión:</strong> El aspect <code>@CheckOwnership</code> extrae por reflexión el claim <code>sub</code> del token y lo compara con el campo <code>reserva.usuarioAuth0Id</code> antes de ejecutar cualquier mutación.
                  </div>
                </div>
              </div>
            )}
          </div>
        </div>

        <div style={{ padding: '14px 24px', background: 'rgba(15, 23, 42, 0.95)', borderTop: '1px solid var(--glass-border)', display: 'flex', justifyContent: 'flex-end' }}>
          <button onClick={() => setShowTokenModal(false)} className="btn btn-secondary">
            Cerrar Inspector
          </button>
        </div>
      </div>
    </div>
  );
};
