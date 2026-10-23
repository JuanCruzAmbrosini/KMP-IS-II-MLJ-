import React, { useState, useEffect } from 'react';
import { apiClient } from '../../api/apiClient';
import { Shield, ShieldAlert, Activity, RefreshCw, Lock, Terminal } from 'lucide-react';

export const SecurityAuditView = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchLogs = async () => {
    try {
      setLoading(true);
      const res = await apiClient.getAuditLogs();
      setLogs(res.data);
    } catch (err) {
      console.warn('Error fetching audit logs:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, []);

  const rateLimitStatus = apiClient.getRateLimitStatus();

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Security Overview Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '14px' }}>
        {/* Token Bucket Monitor */}
        <div className="glass-panel" style={{ padding: '16px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Token Bucket Rate Limiter</span>
            <Activity size={16} color="#10b981" />
          </div>
          <div style={{ fontSize: '1.4rem', fontWeight: 800, color: '#10b981', fontFamily: 'var(--font-mono)' }}>
            {rateLimitStatus.tokensGeneral} / {rateLimitStatus.maxGeneral} <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>req/min</span>
          </div>
          <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>
            Límite reservas críticas: <strong style={{ color: '#fbbf24' }}>{rateLimitStatus.tokensReservas}/{rateLimitStatus.maxReservas}</strong> por minuto
          </div>
        </div>

        {/* BOLA / IDOR Inspector */}
        <div className="glass-panel" style={{ padding: '16px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Protección BOLA / IDOR</span>
            <Lock size={16} color="#c084fc" />
          </div>
          <div style={{ fontSize: '1.1rem', fontWeight: 800, color: '#c084fc' }}>
            @CheckOwnership Activo
          </div>
          <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>
            Navegación reflexiva de grafo de propiedades sobre <code style={{ color: '#a7f3d0' }}>reserva.usuario.auth0Id</code>
          </div>
        </div>

        {/* SSL/TLS */}
        <div className="glass-panel" style={{ padding: '16px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Cifrado SSL / TLS 1.3</span>
            <Shield size={16} color="#38bdf8" />
          </div>
          <div style={{ fontSize: '1.1rem', fontWeight: 800, color: '#38bdf8' }}>
            Puerto 8443 (PKCS12)
          </div>
          <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>
            HSTS activo y cookies estrictas SameSite=Strict
          </div>
        </div>
      </div>

      {/* Security Audit Trail Table (@AuditAction) */}
      <div className="glass-panel" style={{ padding: '20px', overflow: 'hidden' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '16px' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Terminal size={18} color="#38bdf8" />
            <h3 style={{ fontSize: '1rem', fontWeight: 800, color: 'var(--text-main)' }}>
              Pistas de Auditoría (@AuditAction & Intercepciones de Seguridad)
            </h3>
          </div>
          <button onClick={fetchLogs} className="btn btn-secondary btn-sm" disabled={loading} style={{ gap: '6px' }}>
            <RefreshCw size={14} className={loading ? 'spin' : ''} /> Actualizar Logs
          </button>
        </div>

        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.775rem' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--glass-border)', color: 'var(--text-muted)', textAlign: 'left' }}>
                <th style={{ padding: '8px 12px' }}>Timestamp</th>
                <th style={{ padding: '8px 12px' }}>Actor</th>
                <th style={{ padding: '8px 12px' }}>Entidad</th>
                <th style={{ padding: '8px 12px' }}>Acción</th>
                <th style={{ padding: '8px 12px' }}>Detalle</th>
                <th style={{ padding: '8px 12px' }}>IP</th>
              </tr>
            </thead>
            <tbody>
              {logs.map(log => {
                const isBolaAlert = log.accion.includes('BOLA') || log.accion.includes('DENEGADO');

                return (
                  <tr
                    key={log.id}
                    style={{
                      borderBottom: '1px solid rgba(255, 255, 255, 0.04)',
                      background: isBolaAlert ? 'rgba(239, 68, 68, 0.08)' : 'transparent'
                    }}
                  >
                    <td style={{ padding: '10px 12px', fontFamily: 'var(--font-mono)', color: 'var(--text-dim)', whiteSpace: 'nowrap' }}>
                      {new Date(log.timestamp).toLocaleTimeString()}
                    </td>
                    <td style={{ padding: '10px 12px', fontWeight: 600, color: 'var(--text-main)' }}>
                      {log.actor}
                    </td>
                    <td style={{ padding: '10px 12px', color: 'var(--text-muted)' }}>
                      {log.entidad} #{log.entidadId}
                    </td>
                    <td style={{ padding: '10px 12px' }}>
                      <span
                        className="mono-tag"
                        style={{
                          color: isBolaAlert ? '#f87171' : '#34d399',
                          borderColor: isBolaAlert ? 'rgba(239, 68, 68, 0.4)' : 'rgba(16, 185, 129, 0.4)'
                        }}
                      >
                        {log.accion}
                      </span>
                    </td>
                    <td style={{ padding: '10px 12px', color: isBolaAlert ? '#fca5a5' : 'var(--text-muted)' }}>
                      {log.detalle}
                    </td>
                    <td style={{ padding: '10px 12px', fontFamily: 'var(--font-mono)', color: 'var(--text-dim)' }}>
                      {log.ip}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
