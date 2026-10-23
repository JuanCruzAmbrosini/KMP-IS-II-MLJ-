import React from 'react';
import { useRestaurant } from '../../context/RestaurantContext';
import { useAuth } from '../../context/AuthContext';
import { EstadoMesa, EstadoMesaConfig, EstadoReserva } from '../../api/types';
import { Sparkles, Utensils, CheckCircle2, AlertTriangle, Users, Clock } from 'lucide-react';

export const WaiterDashboard = () => {
  const { mesasDelSalon, cambiarEstadoMesa, reservas, activeSalon } = useRestaurant();
  const { currentUser } = useAuth();

  return (
    <div style={{ maxWidth: '1080px', margin: '24px auto', padding: '0 20px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header Info */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ fontSize: '1.35rem', fontWeight: 800, color: 'var(--text-main)', display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Utensils size={20} color="#c084fc" /> Comandas y Estado de Mesas - {activeSalon?.nombre}
          </h2>
          <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
            Mozo en Turno: <strong style={{ color: '#c084fc' }}>{currentUser.nombre}</strong> | Control táctil de rotación rápida
          </p>
        </div>
      </div>

      {/* Grid of Tables for Service */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: '16px' }}>
        {mesasDelSalon.map(mesa => {
          const config = EstadoMesaConfig[mesa.estado] || EstadoMesaConfig.LIBRE;
          const reserva = reservas.find(r => r.mesaId === mesa.id && (r.estado === EstadoReserva.CONFIRMADA || r.estado === EstadoReserva.EN_CURSO));

          return (
            <div
              key={mesa.id}
              className="glass-panel"
              style={{
                padding: '18px',
                borderLeft: `4px solid ${config.borderColor}`,
                display: 'flex',
                flexDirection: 'column',
                gap: '12px'
              }}
            >
              <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <div style={{ width: '38px', height: '38px', borderRadius: '10px', background: config.bgColor, border: `1px solid ${config.borderColor}`, display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 800, fontFamily: 'var(--font-mono)', color: config.color, fontSize: '1.1rem' }}>
                    #{mesa.numero}
                  </div>
                  <div>
                    <div style={{ fontSize: '0.95rem', fontWeight: 700, color: 'var(--text-main)' }}>
                      Mesa #{mesa.numero}
                    </div>
                    <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>
                      Capacidad: {mesa.capacidadMinima} a {mesa.capacidadMaxima} comensales
                    </div>
                  </div>
                </div>

                <span className={config.badgeClass} style={{ padding: '3px 8px', borderRadius: '4px', fontSize: '0.725rem', fontWeight: 700 }}>
                  {config.label}
                </span>
              </div>

              {/* Seated details or reservation */}
              {reserva ? (
                <div style={{ background: 'var(--bg-dark-900)', padding: '10px 12px', borderRadius: 'var(--radius-sm)', border: '1px solid var(--glass-border)', fontSize: '0.775rem' }}>
                  <div style={{ fontWeight: 700, color: '#fbbf24', marginBottom: '2px' }}>
                    Cliente: {reserva.clienteNombre} ({reserva.comensales} personas)
                  </div>
                  {reserva.notasEspeciales && (
                    <div style={{ color: '#f87171', display: 'flex', alignItems: 'center', gap: '4px', marginTop: '4px' }}>
                      <AlertTriangle size={12} /> {reserva.notasEspeciales}
                    </div>
                  )}
                </div>
              ) : (
                <div style={{ fontSize: '0.75rem', color: 'var(--text-dim)', fontStyle: 'italic' }}>
                  Sin reserva asignada en este momento
                </div>
              )}

              {/* Fast 1-Tap Action Buttons for Waiters */}
              <div style={{ display: 'flex', gap: '8px', marginTop: 'auto', paddingTop: '8px', borderTop: '1px solid var(--glass-border)' }}>
                {mesa.estado === EstadoMesa.OCUPADA && (
                  <button
                    onClick={() => cambiarEstadoMesa(mesa.id, EstadoMesa.EN_LIMPIEZA)}
                    className="btn btn-secondary btn-sm"
                    style={{ flex: 1, borderColor: 'rgba(59, 130, 246, 0.5)', color: '#60a5fa' }}
                  >
                    <Sparkles size={14} /> Pasar a Limpieza
                  </button>
                )}

                {mesa.estado === EstadoMesa.EN_LIMPIEZA && (
                  <button
                    onClick={() => cambiarEstadoMesa(mesa.id, EstadoMesa.LIBRE)}
                    className="btn btn-primary btn-sm"
                    style={{ flex: 1 }}
                  >
                    <CheckCircle2 size={14} /> Mesa Desinfectada (Libre)
                  </button>
                )}

                {mesa.estado === EstadoMesa.LIBRE && (
                  <button
                    onClick={() => cambiarEstadoMesa(mesa.id, EstadoMesa.OCUPADA, mesa.capacidadMinima)}
                    className="btn btn-secondary btn-sm"
                    style={{ flex: 1, borderColor: 'rgba(239, 68, 68, 0.4)', color: '#f87171' }}
                  >
                    <Utensils size={14} /> Sentar Clientes
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};
