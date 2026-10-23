import React, { useRef } from 'react';
import { useRestaurant } from '../../context/RestaurantContext';
import { useAuth } from '../../context/AuthContext';
import { TableNode } from './TableNode';
import { TableDetailModal } from './TableDetailModal';
import { EstadoMesa, EstadoMesaConfig, EstadoReserva } from '../../api/types';
import { Maximize2, Move, HelpCircle, Eye } from 'lucide-react';

export const FloorPlanFudo = ({ isEditMode = false }) => {
  const { activeSalon, mesasDelSalon, reservas, setSelectedMesa, moverMesa } = useRestaurant();
  const { isAdmin } = useAuth();
  const viewportRef = useRef(null);

  // Totales de estados en el salón actual
  const stats = {
    total: mesasDelSalon.length,
    libres: mesasDelSalon.filter(m => m.estado === EstadoMesa.LIBRE).length,
    reservadas: mesasDelSalon.filter(m => m.estado === EstadoMesa.RESERVADA).length,
    ocupadas: mesasDelSalon.filter(m => m.estado === EstadoMesa.OCUPADA).length,
    limpieza: mesasDelSalon.filter(m => m.estado === EstadoMesa.EN_LIMPIEZA).length
  };

  // Drag & drop handlers para modo Admin
  const handleDragStart = (e, mesa) => {
    if (!isAdmin || !isEditMode) return;
    e.dataTransfer.setData('text/plain', JSON.stringify({
      id: mesa.id,
      offsetX: e.nativeEvent.offsetX,
      offsetY: e.nativeEvent.offsetY
    }));
  };

  const handleDragOver = (e) => {
    if (!isAdmin || !isEditMode) return;
    e.preventDefault();
  };

  const handleDrop = (e) => {
    if (!isAdmin || !isEditMode) return;
    e.preventDefault();
    try {
      const data = JSON.parse(e.dataTransfer.getData('text/plain'));
      const rect = viewportRef.current.getBoundingClientRect();
      const newX = e.clientX - rect.left - data.offsetX;
      const newY = e.clientY - rect.top - data.offsetY;
      moverMesa(data.id, Math.max(20, newX), Math.max(20, newY));
    } catch (err) {
      console.warn('Drag drop error:', err);
    }
  };

  return (
    <div className="floor-plan-container">
      {/* Floor Plan Header */}
      <div className="floor-plan-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <div>
            <h2 style={{ fontSize: '1.05rem', fontWeight: 800, color: 'var(--text-main)' }}>
              {activeSalon ? activeSalon.nombre : 'Plano del Salón'}
            </h2>
            <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>
              {activeSalon?.descripcion}
            </div>
          </div>
          {isEditMode && (
            <span style={{ background: 'rgba(245, 158, 11, 0.2)', color: '#fbbf24', border: '1px solid rgba(245, 158, 11, 0.4)', padding: '2px 8px', borderRadius: '4px', fontSize: '0.75rem', fontWeight: 700, display: 'flex', alignItems: 'center', gap: '4px' }}>
              <Move size={12} /> Modo Editor Activo (Arrastra mesas)
            </span>
          )}
        </div>

        {/* Legend */}
        <div className="floor-plan-legend">
          <div className="legend-item">
            <span className="legend-color-dot" style={{ background: EstadoMesaConfig.LIBRE.color }} />
            <span>Libres ({stats.libres})</span>
          </div>
          <div className="legend-item">
            <span className="legend-color-dot" style={{ background: EstadoMesaConfig.RESERVADA.color }} />
            <span>Reservadas ({stats.reservadas})</span>
          </div>
          <div className="legend-item">
            <span className="legend-color-dot" style={{ background: EstadoMesaConfig.OCUPADA.color }} />
            <span>Ocupadas ({stats.ocupadas})</span>
          </div>
          <div className="legend-item">
            <span className="legend-color-dot" style={{ background: EstadoMesaConfig.EN_LIMPIEZA.color }} />
            <span>Limpieza ({stats.limpieza})</span>
          </div>
        </div>
      </div>

      {/* Visual Canvas Area */}
      <div
        ref={viewportRef}
        className="floor-plan-viewport"
        onDragOver={handleDragOver}
        onDrop={handleDrop}
      >
        {/* Architectural Landmarks */}
        <div className="architectural-zone zone-barra">
          Barra & Coctelería
        </div>
        <div className="architectural-zone zone-cocina">
          Pase de Cocina
        </div>
        <div className="architectural-zone zone-ingreso">
          Entrada Principal
        </div>

        {/* Table Nodes */}
        {mesasDelSalon.map(mesa => {
          const reserva = reservas.find(r => 
            r.mesaId === mesa.id && 
            (r.estado === EstadoReserva.CONFIRMADA || r.estado === EstadoReserva.EN_CURSO)
          );
          return (
            <TableNode
              key={mesa.id}
              mesa={mesa}
              reserva={reserva}
              onClick={m => setSelectedMesa(m)}
              isDraggable={isAdmin && isEditMode}
              onDragStart={handleDragStart}
            />
          );
        })}
      </div>

      {/* Table Detail Modal Component */}
      <TableDetailModal />
    </div>
  );
};
