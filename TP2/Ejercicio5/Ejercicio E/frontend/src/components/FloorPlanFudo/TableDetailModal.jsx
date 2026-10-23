import React, { useState } from 'react';
import { useRestaurant } from '../../context/RestaurantContext';
import { useAuth } from '../../context/AuthContext';
import { EstadoMesa, EstadoMesaConfig, EstadoReserva } from '../../api/types';
import { X, Users, User, Clock, UtensilsCrossed, Sparkles, AlertCircle, Ban, CheckCircle2 } from 'lucide-react';

export const TableDetailModal = () => {
  const { selectedMesa, setSelectedMesa, cambiarEstadoMesa, reservas, hacerCheckIn, finalizarReserva, marcarNoShow } = useRestaurant();
  const { currentUser, isRecepcion, isMozo, isAdmin } = useAuth();
  const [comensalesInput, setComensalesInput] = useState(2);
  const [isUpdating, setIsUpdating] = useState(false);

  if (!selectedMesa) return null;

  const canModify = isRecepcion || isMozo || isAdmin;
  const config = EstadoMesaConfig[selectedMesa.estado] || EstadoMesaConfig.LIBRE;

  // Buscar reserva asociada a esta mesa para hoy
  const reservaAsociada = reservas.find(r => 
    r.mesaId === selectedMesa.id && 
    (r.estado === EstadoReserva.CONFIRMADA || r.estado === EstadoReserva.EN_CURSO)
  );

  const handleEstadoChange = async (nuevoEstado) => {
    try {
      setIsUpdating(true);
      await cambiarEstadoMesa(selectedMesa.id, nuevoEstado, nuevoEstado === EstadoMesa.OCUPADA ? Number(comensalesInput) : 0);
    } catch (err) {
      // toast ya notificado en context
    } finally {
      setIsUpdating(false);
    }
  };

  const handleCheckIn = async () => {
    if (!reservaAsociada) return;
    try {
      setIsUpdating(true);
      await hacerCheckIn(reservaAsociada.id);
    } finally {
      setIsUpdating(false);
    }
  };

  const handleFinalizar = async () => {
    if (!reservaAsociada) return;
    try {
      setIsUpdating(true);
      await finalizarReserva(reservaAsociada.id);
    } finally {
      setIsUpdating(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={() => setSelectedMesa(null)}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        {/* Modal Header */}
        <div style={{ padding: '20px 24px', borderBottom: '1px solid var(--glass-border)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <div style={{ width: '44px', height: '44px', borderRadius: '12px', background: config.bgColor, border: `2px solid ${config.borderColor}`, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.2rem', fontWeight: 800, color: config.color, fontFamily: 'var(--font-mono)' }}>
              #{selectedMesa.numero}
            </div>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <h3 style={{ fontSize: '1.15rem', fontWeight: 800, color: 'var(--text-main)' }}>
                  Mesa #{selectedMesa.numero}
                </h3>
                <span className={config.badgeClass} style={{ padding: '2px 8px', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 700 }}>
                  {config.label}
                </span>
              </div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                Forma: {selectedMesa.forma} | Capacidad: {selectedMesa.capacidadMinima} a {selectedMesa.capacidadMaxima} comensales
              </div>
            </div>
          </div>
          <button onClick={() => setSelectedMesa(null)} style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}>
            <X size={20} />
          </button>
        </div>

        {/* Modal Body */}
        <div style={{ padding: '20px 24px', display: 'flex', flexDirection: 'column', gap: '18px' }}>
          {/* Waiter & Notes */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px', background: 'var(--bg-dark-900)', padding: '12px 16px', borderRadius: 'var(--radius-md)', border: '1px solid var(--glass-border)' }}>
            <div>
              <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>Mozo asignado</div>
              <div style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--text-main)' }}>
                {selectedMesa.mozoAsignado || 'Sin asignar'}
              </div>
            </div>
            <div>
              <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>Ubicación en plano</div>
              <div style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--text-main)', fontFamily: 'var(--font-mono)' }}>
                X: {selectedMesa.x}px, Y: {selectedMesa.y}px
              </div>
            </div>
          </div>

          {/* Associated Booking Info Card */}
          {reservaAsociada ? (
            <div style={{ background: 'rgba(245, 158, 11, 0.08)', border: '1px solid rgba(245, 158, 11, 0.25)', borderRadius: 'var(--radius-md)', padding: '16px' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '8px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                  <UtensilsCrossed size={16} color="#fbbf24" />
                  <span style={{ fontSize: '0.85rem', fontWeight: 700, color: '#fbbf24' }}>
                    Reserva Activa: {reservaAsociada.codigoReserva}
                  </span>
                </div>
                <span className="mono-tag" style={{ color: '#fbbf24' }}>
                  {reservaAsociada.estado}
                </span>
              </div>

              <div style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--text-main)', marginBottom: '4px' }}>
                {reservaAsociada.clienteNombre} ({reservaAsociada.comensales} personas)
              </div>
              <div style={{ fontSize: '0.775rem', color: 'var(--text-muted)' }}>
                Tel: {reservaAsociada.clienteTelefono} | Turno: {reservaAsociada.turnoNombre}
              </div>
              {reservaAsociada.notasEspeciales && (
                <div style={{ marginTop: '8px', padding: '8px 10px', background: 'rgba(0,0,0,0.2)', borderRadius: '6px', fontSize: '0.775rem', color: '#fcd34d', display: 'flex', gap: '6px', alignItems: 'center' }}>
                  <AlertCircle size={14} style={{ flexShrink: 0 }} />
                  <span>{reservaAsociada.notasEspeciales}</span>
                </div>
              )}

              {/* Quick actions for host on active booking */}
              {canModify && (
                <div style={{ marginTop: '12px', display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                  {reservaAsociada.estado === EstadoReserva.CONFIRMADA && (
                    <button onClick={handleCheckIn} disabled={isUpdating} className="btn btn-primary btn-sm">
                      <CheckCircle2 size={14} />
                      Hacer Check-In (Sentar a la mesa)
                    </button>
                  )}
                  {reservaAsociada.estado === EstadoReserva.EN_CURSO && (
                    <button onClick={handleFinalizar} disabled={isUpdating} className="btn btn-purple btn-sm">
                      <Sparkles size={14} />
                      Finalizar Servicio (Pasar a Limpieza)
                    </button>
                  )}
                  <button onClick={() => marcarNoShow(reservaAsociada.id)} disabled={isUpdating} className="btn btn-danger btn-sm">
                    <Ban size={14} />
                    Marcar No-Show
                  </button>
                </div>
              )}
            </div>
          ) : (
            <div style={{ padding: '12px 16px', background: 'rgba(255, 255, 255, 0.02)', borderRadius: 'var(--radius-md)', border: '1px dashed var(--glass-border)', fontSize: '0.8rem', color: 'var(--text-muted)', textAlign: 'center' }}>
              No hay reservas confirmadas asignadas a esta mesa en este turno.
            </div>
          )}

          {/* Quick Status Toggles (Section 4.4 / Fudo Quick Toggle) */}
          {canModify && (
            <div>
              <div style={{ fontSize: '0.825rem', fontWeight: 700, color: 'var(--text-main)', marginBottom: '10px' }}>
                Cambiar Estado de la Mesa (Control Fudo)
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(130px, 1fr))', gap: '8px' }}>
                <button
                  onClick={() => handleEstadoChange(EstadoMesa.LIBRE)}
                  disabled={isUpdating || selectedMesa.estado === EstadoMesa.LIBRE}
                  className="btn btn-secondary btn-sm"
                  style={{ borderColor: 'rgba(16, 185, 129, 0.4)', color: '#34d399' }}
                >
                  <span className="status-dot" style={{ background: '#10b981' }} />
                  Marcar Libre
                </button>

                <button
                  onClick={() => handleEstadoChange(EstadoMesa.OCUPADA)}
                  disabled={isUpdating || selectedMesa.estado === EstadoMesa.OCUPADA}
                  className="btn btn-secondary btn-sm"
                  style={{ borderColor: 'rgba(239, 68, 68, 0.4)', color: '#f87171' }}
                >
                  <span className="status-dot" style={{ background: '#ef4444' }} />
                  Ocupar Mesa
                </button>

                <button
                  onClick={() => handleEstadoChange(EstadoMesa.EN_LIMPIEZA)}
                  disabled={isUpdating || selectedMesa.estado === EstadoMesa.EN_LIMPIEZA}
                  className="btn btn-secondary btn-sm"
                  style={{ borderColor: 'rgba(59, 130, 246, 0.4)', color: '#60a5fa' }}
                >
                  <span className="status-dot" style={{ background: '#3b82f6' }} />
                  En Limpieza
                </button>

                <button
                  onClick={() => handleEstadoChange(EstadoMesa.FUERA_DE_SERVICIO)}
                  disabled={isUpdating || selectedMesa.estado === EstadoMesa.FUERA_DE_SERVICIO}
                  className="btn btn-secondary btn-sm"
                  style={{ borderColor: 'rgba(100, 116, 139, 0.4)', color: '#94a3b8' }}
                >
                  <span className="status-dot" style={{ background: '#64748b' }} />
                  Fuera Servicio
                </button>
              </div>

              {selectedMesa.estado !== EstadoMesa.OCUPADA && (
                <div style={{ marginTop: '12px', display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Comensales al ocupar:</span>
                  <input
                    type="number"
                    min="1"
                    max={selectedMesa.capacidadMaxima}
                    value={comensalesInput}
                    onChange={e => setComensalesInput(e.target.value)}
                    style={{ width: '60px', padding: '4px 8px', background: 'var(--bg-dark-900)', border: '1px solid var(--glass-border)', color: 'white', borderRadius: '4px', textAlign: 'center' }}
                  />
                </div>
              )}
            </div>
          )}
        </div>

        {/* Modal Footer */}
        <div style={{ padding: '14px 24px', background: 'rgba(15, 23, 42, 0.95)', borderTop: '1px solid var(--glass-border)', display: 'flex', justifyContent: 'flex-end' }}>
          <button onClick={() => setSelectedMesa(null)} className="btn btn-secondary">
            Cerrar
          </button>
        </div>
      </div>
    </div>
  );
};
