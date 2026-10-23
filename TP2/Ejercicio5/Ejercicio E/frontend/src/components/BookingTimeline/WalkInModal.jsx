import React, { useState } from 'react';
import { useRestaurant } from '../../context/RestaurantContext';
import { useAuth } from '../../context/AuthContext';
import { X, Users, DoorOpen, Sparkles, MapPin } from 'lucide-react';

export const WalkInModal = () => {
  const { showWalkInModal, setShowWalkInModal, crearReserva, salones, activeSalonId, activeTurnoId, selectedDate } = useRestaurant();
  const { currentUser } = useAuth();

  const [clienteNombre, setClienteNombre] = useState('');
  const [comensales, setComensales] = useState(2);
  const [salonId, setSalonId] = useState(activeSalonId);
  const [notasEspeciales, setNotasEspeciales] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  if (!showWalkInModal) return null;

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!clienteNombre.trim()) return;

    try {
      setIsSubmitting(true);
      await crearReserva({
        clienteNombre,
        comensales: Number(comensales),
        salonId: Number(salonId),
        turnoId: activeTurnoId,
        fecha: selectedDate,
        tipo: 'WALK_IN',
        notasEspeciales: notasEspeciales ? `[Walk-in en Puerta] ${notasEspeciales}` : '[Walk-in en Puerta]'
      });
      setShowWalkInModal(false);
      setClienteNombre('');
      setNotasEspeciales('');
    } catch (err) {
      // Error manejado en context
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={() => setShowWalkInModal(false)}>
      <div className="modal-content" onClick={e => e.stopPropagation()} style={{ maxWidth: '520px' }}>
        <div style={{ padding: '20px 24px', borderBottom: '1px solid var(--glass-border)', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
            <div style={{ width: '40px', height: '40px', borderRadius: '10px', background: 'rgba(245, 158, 11, 0.15)', border: '1px solid rgba(245, 158, 11, 0.3)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fbbf24' }}>
              <DoorOpen size={20} />
            </div>
            <div>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--text-main)' }}>
                Recepción Inmediata (Walk-In)
              </h3>
              <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                Asignación inteligente y asiento directo a comensales en puerta
              </p>
            </div>
          </div>
          <button onClick={() => setShowWalkInModal(false)} style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer' }}>
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} style={{ padding: '20px 24px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
          {/* Nombre */}
          <div>
            <label className="wizard-label">Nombre del Cliente o Grupo *</label>
            <input
              type="text"
              required
              placeholder="Ej. Familia Rodríguez / Juan Perez"
              className="wizard-input"
              value={clienteNombre}
              onChange={e => setClienteNombre(e.target.value)}
              autoFocus
            />
          </div>

          {/* Comensales y Salón */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1.3fr', gap: '12px' }}>
            <div>
              <label className="wizard-label">Comensales *</label>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Users size={16} color="var(--text-muted)" />
                <input
                  type="number"
                  min="1"
                  max="12"
                  required
                  className="wizard-input"
                  value={comensales}
                  onChange={e => setComensales(e.target.value)}
                />
              </div>
            </div>

            <div>
              <label className="wizard-label">Salón Preferido *</label>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <MapPin size={16} color="var(--text-muted)" />
                <select
                  className="wizard-select"
                  value={salonId}
                  onChange={e => setSalonId(Number(e.target.value))}
                >
                  {salones.map(s => (
                    <option key={s.id} value={s.id}>{s.nombre}</option>
                  ))}
                </select>
              </div>
            </div>
          </div>

          {/* Notas */}
          <div>
            <label className="wizard-label">Notas o Requerimientos (Opcional)</label>
            <input
              type="text"
              placeholder="Ej. Silla de bebé / Ubicación tranquila"
              className="wizard-input"
              value={notasEspeciales}
              onChange={e => setNotasEspeciales(e.target.value)}
            />
          </div>

          {/* Template Method Algorithm Preview */}
          <div className="algorithm-callout" style={{ margin: '4px 0' }}>
            <Sparkles size={18} color="#a855f7" style={{ flexShrink: 0, marginTop: '2px' }} />
            <div style={{ fontSize: '0.775rem', color: 'var(--text-muted)' }}>
              <strong style={{ color: '#c084fc' }}>Motor Template Method (Fudo):</strong> Asigna automáticamente la mejor mesa libre minimizando el desperdicio de sillas con la fórmula <span style={{ fontFamily: 'var(--font-mono)', color: '#a7f3d0' }}>min(Capacidad - Comensales)</span> y pasa la mesa directamente a <span style={{ color: '#f87171', fontWeight: 700 }}>OCUPADA</span>.
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '8px' }}>
            <button type="button" onClick={() => setShowWalkInModal(false)} className="btn btn-secondary">
              Cancelar
            </button>
            <button type="submit" disabled={isSubmitting} className="btn btn-gold">
              {isSubmitting ? 'Asignando mesa...' : 'Confirmar y Sentar Ahora'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};
