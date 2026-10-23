import React, { useState } from 'react';
import { useRestaurant } from '../../context/RestaurantContext';
import { useAuth } from '../../context/AuthContext';
import { FloorPlanFudo } from '../FloorPlanFudo/FloorPlanFudo';
import { SecurityAuditView } from './SecurityAuditView';
import { EstadoMesa } from '../../api/types';
import { LayoutGrid, ShieldCheck, Clock, Plus, BarChart3, Settings2, Info, X } from 'lucide-react';
import { apiClient } from '../../api/apiClient';

export const AdminPanel = () => {
  const { mesas, reservas, turnos, salones, activeSalonId, refreshData, addToast } = useRestaurant();
  const [activeTab, setActiveTab] = useState('plano'); // 'plano' | 'turnos' | 'seguridad'
  const [showAddTableModal, setShowAddTableModal] = useState(false);

  // New table form state
  const [numeroMesa, setNumeroMesa] = useState('');
  const [capacidadMin, setCapacidadMin] = useState(2);
  const [capacidadMax, setCapacidadMax] = useState(4);
  const [formaMesa, setFormaMesa] = useState('cuadrada');
  const [mozoAsignado, setMozoAsignado] = useState('Carlos Benítez');

  // Stats calculation
  const totalMesas = mesas.length;
  const mesasOcupadas = mesas.filter(m => m.estado === EstadoMesa.OCUPADA).length;
  const tasaOcupacion = totalMesas > 0 ? Math.round((mesasOcupadas / totalMesas) * 100) : 0;
  const reservasHoy = reservas.length;

  const handleCrearMesa = async (e) => {
    e.preventDefault();
    try {
      await apiClient.crearMesa({
        salonId: activeSalonId,
        numero: Number(numeroMesa) || (totalMesas + 1),
        capacidadMinima: Number(capacidadMin),
        capacidadMaxima: Number(capacidadMax),
        forma: formaMesa,
        mozoAsignado,
        x: 120 + Math.floor(Math.random() * 200),
        y: 120 + Math.floor(Math.random() * 150),
        ancho: formaMesa === 'rectangular' ? 120 : 85,
        alto: 85
      });
      await refreshData();
      addToast('success', 'Mesa Creada', `Mesa #${numeroMesa} agregada al plano`);
      setShowAddTableModal(false);
      setNumeroMesa('');
    } catch (err) {
      addToast('error', 'Error al crear mesa', err.message);
    }
  };

  return (
    <div style={{ maxWidth: '1280px', margin: '20px auto', padding: '0 24px', display: 'flex', flexDirection: 'column', gap: '20px' }}>
      {/* Top Operational KPI Cards */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '14px' }}>
        <div className="glass-panel" style={{ padding: '16px' }}>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Tasa de Ocupación Salones</div>
          <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#10b981', fontFamily: 'var(--font-mono)' }}>
            {tasaOcupacion}%
          </div>
          <div style={{ fontSize: '0.7rem', color: 'var(--text-dim)' }}>
            {mesasOcupadas} de {totalMesas} mesas en servicio
          </div>
        </div>

        <div className="glass-panel" style={{ padding: '16px' }}>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Reservas Totales Hoy</div>
          <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#38bdf8', fontFamily: 'var(--font-mono)' }}>
            {reservasHoy}
          </div>
          <div style={{ fontSize: '0.7rem', color: 'var(--text-dim)' }}>
            {reservas.filter(r => r.tipo === 'WALK_IN').length} walk-ins registrados
          </div>
        </div>

        <div className="glass-panel" style={{ padding: '16px' }}>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Salones Configurados</div>
          <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#fbbf24', fontFamily: 'var(--font-mono)' }}>
            {salones.length}
          </div>
          <div style={{ fontSize: '0.7rem', color: 'var(--text-dim)' }}>
            Principal, Terraza, Patio, VIP
          </div>
        </div>

        <div className="glass-panel" style={{ padding: '16px' }}>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Turnos Operativos</div>
          <div style={{ fontSize: '1.6rem', fontWeight: 800, color: '#c084fc', fontFamily: 'var(--font-mono)' }}>
            {turnos.length}
          </div>
          <div style={{ fontSize: '0.7rem', color: 'var(--text-dim)' }}>
            Almuerzos y Cenas
          </div>
        </div>
      </div>

      {/* Admin Tab Navigation */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderBottom: '1px solid var(--glass-border)', paddingBottom: '10px', flexWrap: 'wrap', gap: '10px' }}>
        <div style={{ display: 'flex', gap: '8px' }}>
          <button
            className={`btn ${activeTab === 'plano' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setActiveTab('plano')}
            style={{ gap: '6px' }}
          >
            <LayoutGrid size={16} /> Diseñador de Plano Fudo
          </button>
          <button
            className={`btn ${activeTab === 'turnos' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setActiveTab('turnos')}
            style={{ gap: '6px' }}
          >
            <Clock size={16} /> Configuración de Turnos
          </button>
          <button
            className={`btn ${activeTab === 'seguridad' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setActiveTab('seguridad')}
            style={{ gap: '6px' }}
          >
            <ShieldCheck size={16} /> Auditoría & Seguridad API
          </button>
        </div>

        {activeTab === 'plano' && (
          <button onClick={() => setShowAddTableModal(true)} className="btn btn-gold btn-sm" style={{ gap: '6px' }}>
            <Plus size={16} /> Agregar Nueva Mesa
          </button>
        )}
      </div>

      {/* Tab 1: Floor Plan Editor */}
      {activeTab === 'plano' && (
        <div>
          <div style={{ marginBottom: '10px', fontSize: '0.8rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '6px' }}>
            <Info size={15} color="#38bdf8" /> <strong style={{ color: 'white' }}>Modo Diseñador:</strong> Puedes arrastrar cualquier mesa para reubicarla en las coordenadas (x, y). Los cambios se guardan dinámicamente mediante <code style={{ color: '#a7f3d0' }}>PATCH /api/v1/mesas/:id/posicion</code>.
          </div>
          <FloorPlanFudo isEditMode={true} />
        </div>
      )}

      {/* Tab 2: Turnos Config */}
      {activeTab === 'turnos' && (
        <div className="glass-panel" style={{ padding: '24px' }}>
          <h3 style={{ fontSize: '1.1rem', fontWeight: 800, marginBottom: '16px', color: 'var(--text-main)' }}>
            Turnos y Horarios Operativos del Restaurante
          </h3>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '14px' }}>
            {turnos.map(t => (
              <div key={t.id} style={{ background: 'var(--bg-dark-900)', border: '1px solid var(--glass-border)', borderRadius: 'var(--radius-md)', padding: '16px', display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <span style={{ fontWeight: 700, fontSize: '0.95rem', color: 'var(--text-main)' }}>{t.nombre}</span>
                  <span className="mono-tag" style={{ color: '#10b981' }}>Activo</span>
                </div>
                <div style={{ fontSize: '0.85rem', color: '#fbbf24', fontFamily: 'var(--font-mono)' }}>
                  {t.horaInicio} a {t.horaFin}
                </div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  Duración promedio: {t.duracionMinutos} min | Tolerancia: {t.toleranciaMinutos} min
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Tab 3: Security & Audit */}
      {activeTab === 'seguridad' && (
        <SecurityAuditView />
      )}

      {/* Add Table Modal */}
      {showAddTableModal && (
        <div className="modal-overlay" onClick={() => setShowAddTableModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()} style={{ maxWidth: '480px' }}>
            <div style={{ padding: '18px 24px', borderBottom: '1px solid var(--glass-border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--text-main)' }}>
                Agregar Nueva Mesa al Salón
              </h3>
              <button onClick={() => setShowAddTableModal(false)} style={{ background: 'transparent', border: 'none', color: 'var(--text-muted)', cursor: 'pointer', display: 'flex', alignItems: 'center' }}><X size={18} /></button>
            </div>

            <form onSubmit={handleCrearMesa} style={{ padding: '20px 24px', display: 'flex', flexDirection: 'column', gap: '14px' }}>
              <div>
                <label className="wizard-label">Número Identificatorio de Mesa *</label>
                <input
                  type="number"
                  required
                  placeholder="Ej. 15"
                  className="wizard-input"
                  value={numeroMesa}
                  onChange={e => setNumeroMesa(e.target.value)}
                  autoFocus
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div>
                  <label className="wizard-label">Capacidad Mínima</label>
                  <input
                    type="number"
                    min="1"
                    max="10"
                    className="wizard-input"
                    value={capacidadMin}
                    onChange={e => setCapacidadMin(e.target.value)}
                  />
                </div>
                <div>
                  <label className="wizard-label">Capacidad Máxima</label>
                  <input
                    type="number"
                    min="1"
                    max="16"
                    className="wizard-input"
                    value={capacidadMax}
                    onChange={e => setCapacidadMax(e.target.value)}
                  />
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
                <div>
                  <label className="wizard-label">Forma Geométrica</label>
                  <select
                    className="wizard-select"
                    value={formaMesa}
                    onChange={e => setFormaMesa(e.target.value)}
                  >
                    <option value="cuadrada">Cuadrada</option>
                    <option value="redonda">Redonda</option>
                    <option value="rectangular">Rectangular</option>
                  </select>
                </div>

                <div>
                  <label className="wizard-label">Mozo Asignado</label>
                  <input
                    type="text"
                    className="wizard-input"
                    value={mozoAsignado}
                    onChange={e => setMozoAsignado(e.target.value)}
                  />
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '10px' }}>
                <button type="button" onClick={() => setShowAddTableModal(false)} className="btn btn-secondary">
                  Cancelar
                </button>
                <button type="submit" className="btn btn-primary">
                  Crear Mesa en Plano
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
