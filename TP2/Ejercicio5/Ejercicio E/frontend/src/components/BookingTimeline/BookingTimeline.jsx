import React, { useState } from 'react';
import { useRestaurant } from '../../context/RestaurantContext';
import { useAuth } from '../../context/AuthContext';
import { EstadoReserva, EstadoReservaConfig } from '../../api/types';
import { WalkInModal } from './WalkInModal';
import { Clock, Users, CheckCircle2, Sparkles, Ban, ShieldAlert, Phone, AlertCircle, PlusCircle } from 'lucide-react';

export const BookingTimeline = () => {
  const {
    reservas,
    activeTurno,
    activeTurnoId,
    selectedDate,
    hacerCheckIn,
    finalizarReserva,
    marcarNoShow,
    cancelarReserva,
    probarAtaqueBOLA,
    setShowWalkInModal
  } = useRestaurant();

  const { isRecepcion, isAdmin, isCliente, currentUser } = useAuth();
  const [filterState, setFilterState] = useState('TODAS');

  // Filtrar reservas del turno actual o todas las de hoy
  const reservasDelTurno = reservas.filter(r => {
    const matchesTurno = activeTurnoId ? r.turnoId === activeTurnoId : true;
    const matchesFilter = filterState === 'TODAS' ? true : r.estado === filterState;
    return matchesTurno && matchesFilter;
  });

  return (
    <div className="timeline-panel">
      {/* Timeline Header */}
      <div className="timeline-header">
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <Clock size={16} color="#fbbf24" />
            <h3 style={{ fontSize: '0.95rem', fontWeight: 800, color: 'var(--text-main)' }}>
              Línea de Turno
            </h3>
          </div>
          <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>
            {activeTurno?.nombre} ({reservasDelTurno.length} reservas)
          </div>
        </div>

        {isRecepcion && (
          <button
            onClick={() => setShowWalkInModal(true)}
            className="btn btn-gold btn-sm"
            style={{ padding: '4px 8px', fontSize: '0.75rem' }}
          >
            <PlusCircle size={14} /> Walk-In
          </button>
        )}
      </div>

      {/* Filter Tabs */}
      <div style={{ display: 'flex', gap: '4px', padding: '8px 12px', background: 'rgba(15, 23, 42, 0.5)', borderBottom: '1px solid var(--glass-border)', overflowX: 'auto' }}>
        {['TODAS', EstadoReserva.CONFIRMADA, EstadoReserva.EN_CURSO, EstadoReserva.FINALIZADA].map(st => (
          <button
            key={st}
            className={`btn btn-sm ${filterState === st ? 'btn-primary' : 'btn-secondary'}`}
            style={{ padding: '3px 8px', fontSize: '0.725rem' }}
            onClick={() => setFilterState(st)}
          >
            {st === 'TODAS' ? 'Todas' : EstadoReservaConfig[st]?.label || st}
          </button>
        ))}
      </div>

      {/* Bookings List */}
      <div className="timeline-body">
        {reservasDelTurno.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '32px 16px', color: 'var(--text-muted)', fontSize: '0.85rem' }}>
            No hay reservas registradas para este filtro en el turno actual.
          </div>
        ) : (
          reservasDelTurno.map(reserva => {
            const stConfig = EstadoReservaConfig[reserva.estado] || EstadoReservaConfig.CONFIRMADA;
            const esPropia = reserva.usuarioAuth0Id === currentUser.auth0Id;

            return (
              <div key={reserva.id} className="booking-card">
                <div className="booking-card-top">
                  <div>
                    <div className="booking-client-name">{reserva.clienteNombre}</div>
                    <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '4px' }}>
                      <Phone size={11} /> {reserva.clienteTelefono}
                    </div>
                  </div>
                  <span className={stConfig.badgeClass} style={{ padding: '2px 8px', borderRadius: '4px', fontSize: '0.725rem', fontWeight: 700 }}>
                    {stConfig.label}
                  </span>
                </div>

                <div className="booking-meta-row">
                  <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                    <Users size={12} /> {reserva.comensales} personas
                  </span>
                  <span className="mono-tag" style={{ color: '#38bdf8' }}>
                    Mesa #{reserva.mesaNumero || 'Auto'}
                  </span>
                  <span style={{ fontSize: '0.7rem', color: 'var(--text-dim)' }}>
                    {reserva.codigoReserva}
                  </span>
                </div>

                {reserva.notasEspeciales && (
                  <div style={{ fontSize: '0.725rem', color: '#fcd34d', background: 'rgba(245, 158, 11, 0.08)', padding: '4px 8px', borderRadius: '4px', display: 'flex', gap: '4px', alignItems: 'center' }}>
                    <AlertCircle size={12} style={{ flexShrink: 0 }} />
                    <span style={{ overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                      {reserva.notasEspeciales}
                    </span>
                  </div>
                )}

                {/* Quick 1-click Host / Reception Actions */}
                <div className="booking-actions">
                  {reserva.estado === EstadoReserva.CONFIRMADA && (isRecepcion || isAdmin) && (
                    <button
                      onClick={() => hacerCheckIn(reserva.id)}
                      className="btn btn-primary btn-sm"
                      style={{ padding: '4px 8px', fontSize: '0.725rem' }}
                      title="Sentar cliente en mesa asignada"
                    >
                      <CheckCircle2 size={13} /> Check-in
                    </button>
                  )}

                  {reserva.estado === EstadoReserva.EN_CURSO && (isRecepcion || isAdmin) && (
                    <button
                      onClick={() => finalizarReserva(reserva.id)}
                      className="btn btn-purple btn-sm"
                      style={{ padding: '4px 8px', fontSize: '0.725rem' }}
                      title="Mandar mesa a desinfección"
                    >
                      <Sparkles size={13} /> Liberar Mesa
                    </button>
                  )}

                  {reserva.estado === EstadoReserva.CONFIRMADA && (isRecepcion || isAdmin) && (
                    <button
                      onClick={() => marcarNoShow(reserva.id)}
                      className="btn btn-secondary btn-sm"
                      style={{ padding: '4px 8px', fontSize: '0.725rem', color: '#f87171' }}
                      title="Marcar cliente ausente"
                    >
                      <Ban size={13} /> No-Show
                    </button>
                  )}

                  {/* Anti-BOLA / IDOR Interactive Test Button for Evaluators */}
                  {!esPropia && (
                    <button
                      onClick={() => probarAtaqueBOLA(reserva.id)}
                      className="btn btn-secondary btn-sm"
                      style={{ padding: '4px 8px', fontSize: '0.7rem', borderColor: 'rgba(168, 85, 247, 0.4)', color: '#c084fc' }}
                      title="Demuestra cómo @CheckOwnership bloquea que un usuario cancele reservas ajenas"
                    >
                      <ShieldAlert size={12} /> Test BOLA
                    </button>
                  )}

                  {/* Cancel Button if own reservation or admin */}
                  {(esPropia || isAdmin || isRecepcion) && reserva.estado === EstadoReserva.CONFIRMADA && (
                    <button
                      onClick={() => cancelarReserva(reserva.id)}
                      className="btn btn-danger btn-sm"
                      style={{ padding: '4px 8px', fontSize: '0.7rem' }}
                    >
                      Cancelar
                    </button>
                  )}
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Walk-in Modal Component */}
      <WalkInModal />
    </div>
  );
};
