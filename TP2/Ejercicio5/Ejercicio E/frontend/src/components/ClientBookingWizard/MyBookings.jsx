import React from 'react';
import { useRestaurant } from '../../context/RestaurantContext';
import { useAuth } from '../../context/AuthContext';
import { EstadoReserva, EstadoReservaConfig } from '../../api/types';
import { Calendar, Clock, MapPin, Users, ShieldAlert, Ban, AlertCircle, Sparkles } from 'lucide-react';

export const MyBookings = () => {
  const { reservas, cancelarReserva, probarAtaqueBOLA } = useRestaurant();
  const { currentUser } = useAuth();

  // Filtrar reservas que pertenezcan a este usuario por su auth0Id
  const misReservas = reservas.filter(r => r.usuarioAuth0Id === currentUser.auth0Id);

  // Buscar una reserva que NO pertenezca a este usuario para el test BOLA
  const reservaAjena = reservas.find(r => r.usuarioAuth0Id !== currentUser.auth0Id && r.estado === EstadoReserva.CONFIRMADA);

  return (
    <div style={{ maxWidth: '820px', margin: '30px auto', padding: '0 20px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Header Info */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '12px' }}>
        <div>
          <h2 style={{ fontSize: '1.35rem', fontWeight: 800, color: 'var(--text-main)' }}>
            Mis Reservas Gastronómicas
          </h2>
          <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
            Comensal: <strong style={{ color: '#38bdf8' }}>{currentUser.nombre}</strong> ({currentUser.email})
          </p>
        </div>

        {/* BOLA Security Simulation Card */}
        {reservaAjena && (
          <div style={{ background: 'rgba(139, 92, 246, 0.1)', border: '1px solid rgba(139, 92, 246, 0.35)', borderRadius: 'var(--radius-md)', padding: '10px 14px', display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div>
              <div style={{ fontSize: '0.75rem', fontWeight: 700, color: '#c084fc', display: 'flex', alignItems: 'center', gap: '4px' }}>
                <ShieldAlert size={14} /> Laboratorio de Seguridad BOLA / IDOR
              </div>
              <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>
                Intentar cancelar la reserva ajena <span style={{ fontFamily: 'var(--font-mono)', color: 'white' }}>{reservaAjena.codigoReserva}</span> de {reservaAjena.clienteNombre}
              </div>
            </div>
            <button
              onClick={() => probarAtaqueBOLA(reservaAjena.id)}
              className="btn btn-secondary btn-sm"
              style={{ borderColor: 'rgba(139, 92, 246, 0.5)', color: '#c084fc', fontSize: '0.75rem' }}
              title="Demuestra que @CheckOwnership bloquea con 403 Forbidden"
            >
              Simular Ataque BOLA
            </button>
          </div>
        )}
      </div>

      {/* Bookings List */}
      {misReservas.length === 0 ? (
        <div className="glass-panel" style={{ padding: '40px', textAlign: 'center', color: 'var(--text-muted)' }}>
          <Calendar size={40} style={{ margin: '0 auto 12px', opacity: 0.4 }} />
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--text-main)', marginBottom: '4px' }}>
            No posees reservas activas
          </h3>
          <p style={{ fontSize: '0.825rem', marginBottom: '16px' }}>
            Utiliza la pestaña "Nueva Reserva" para agendar tu próxima mesa.
          </p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '14px' }}>
          {misReservas.map(reserva => {
            const stConfig = EstadoReservaConfig[reserva.estado] || EstadoReservaConfig.CONFIRMADA;
            const canCancel = reserva.estado === EstadoReserva.CONFIRMADA || reserva.estado === EstadoReserva.PENDIENTE;

            return (
              <div key={reserva.id} className="glass-panel" style={{ padding: '20px 24px', display: 'flex', flexDirection: 'column', gap: '14px' }}>
                <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '12px' }}>
                  <div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '4px' }}>
                      <span style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--text-main)' }}>
                        {reserva.codigoReserva}
                      </span>
                      <span className={stConfig.badgeClass} style={{ padding: '2px 8px', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 700 }}>
                        {stConfig.label}
                      </span>
                    </div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                      Asignada a: <strong style={{ color: '#10b981' }}>Mesa #{reserva.mesaNumero || 'Pendiente'}</strong> en {reserva.salonNombre}
                    </div>
                  </div>

                  {canCancel && (
                    <button
                      onClick={() => cancelarReserva(reserva.id)}
                      className="btn btn-danger btn-sm"
                      style={{ gap: '6px' }}
                    >
                      <Ban size={14} /> Cancelar mi Reserva
                    </button>
                  )}
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(160px, 1fr))', gap: '12px', background: 'var(--bg-dark-900)', padding: '12px 16px', borderRadius: 'var(--radius-md)', border: '1px solid var(--glass-border)', fontSize: '0.8rem' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <Calendar size={16} color="#38bdf8" />
                    <div>
                      <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>Fecha</div>
                      <div style={{ fontWeight: 600 }}>{reserva.fecha}</div>
                    </div>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <Clock size={16} color="#fbbf24" />
                    <div>
                      <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>Turno</div>
                      <div style={{ fontWeight: 600 }}>{reserva.turnoNombre}</div>
                    </div>
                  </div>

                  <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <Users size={16} color="#10b981" />
                    <div>
                      <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>Comensales</div>
                      <div style={{ fontWeight: 600 }}>{reserva.comensales} personas</div>
                    </div>
                  </div>
                </div>

                {reserva.notasEspeciales && (
                  <div style={{ fontSize: '0.775rem', color: '#fcd34d', background: 'rgba(245, 158, 11, 0.08)', padding: '8px 12px', borderRadius: '6px', display: 'flex', gap: '6px', alignItems: 'center' }}>
                    <AlertCircle size={14} style={{ flexShrink: 0 }} />
                    <span>Notas: {reserva.notasEspeciales}</span>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};
