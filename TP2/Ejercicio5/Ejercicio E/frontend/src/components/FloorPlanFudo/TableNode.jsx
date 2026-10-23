import React from 'react';
import { EstadoMesa, EstadoMesaConfig } from '../../api/types';
import { Users, Clock } from 'lucide-react';

export const TableNode = ({ mesa, reserva, isSelected, onClick, isDraggable, onDragStart }) => {
  const config = EstadoMesaConfig[mesa.estado] || EstadoMesaConfig.LIBRE;
  const isOccupied = mesa.estado === EstadoMesa.OCUPADA;
  const isReserved = mesa.estado === EstadoMesa.RESERVADA;

  // Render chairs according to capacity and shape
  const renderChairs = () => {
    const chairs = [];
    // Up / Down chairs
    chairs.push(<div key="c-top" className="chair chair-top" />);
    chairs.push(<div key="c-bottom" className="chair chair-bottom" />);

    if (mesa.capacidadMaxima >= 4) {
      chairs.push(<div key="c-left" className="chair chair-left" />);
      chairs.push(<div key="c-right" className="chair chair-right" />);
    }
    return chairs;
  };

  return (
    <div
      className={`table-node state-${mesa.estado} ${isDraggable ? 'is-draggable' : ''}`}
      style={{
        left: `${mesa.x}px`,
        top: `${mesa.y}px`,
        width: `${mesa.ancho}px`,
        height: `${mesa.alto}px`
      }}
      onClick={() => onClick(mesa)}
      draggable={isDraggable}
      onDragStart={e => onDragStart && onDragStart(e, mesa)}
      title={`Mesa #${mesa.numero} (${config.label}) - Capacidad: ${mesa.capacidadMinima}-${mesa.capacidadMaxima} comensales`}
    >
      {/* Visual Simulated Chairs */}
      {renderChairs()}

      {/* Table Surface */}
      <div className={`table-surface shape-${mesa.forma || 'cuadrada'}`}>
        {/* Occupied elapsed timer pill */}
        {isOccupied && mesa.tiempoOcupacionMinutos > 0 && (
          <div className="table-time-pill" title={`Ocupada hace ${mesa.tiempoOcupacionMinutos} minutos`}>
            <Clock size={10} />
            <span>{mesa.tiempoOcupacionMinutos}m</span>
          </div>
        )}

        {/* Table Number */}
        <span className="table-number">
          #{mesa.numero}
        </span>

        {/* Capacity / Guest Count */}
        <div className="table-meta">
          <Users size={11} />
          <span>
            {isOccupied && mesa.comensalesActuales > 0 
              ? `${mesa.comensalesActuales}/${mesa.capacidadMaxima}`
              : `${mesa.capacidadMaxima}`}
          </span>
        </div>

        {/* Client Name Tag if Reserved or Seated */}
        {reserva && (
          <div className="table-booking-tag" title={reserva.clienteNombre}>
            {reserva.clienteNombre.split(' ')[0]}
          </div>
        )}
      </div>
    </div>
  );
};
