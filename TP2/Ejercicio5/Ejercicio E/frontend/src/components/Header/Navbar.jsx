import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { useRestaurant } from '../../context/RestaurantContext';
import { Utensils, Shield, UserCheck, Calendar, Clock, PlusCircle, RefreshCw, LogOut } from 'lucide-react';
import { Roles } from '../../api/types';

export const Navbar = () => {
  const { currentUser, switchUser, availableUsers, setShowTokenModal, isRecepcion, isAdmin, logout } = useAuth();
  const {
    salones,
    activeSalonId,
    setActiveSalonId,
    turnos,
    activeTurnoId,
    setActiveTurnoId,
    selectedDate,
    setSelectedDate,
    setShowWalkInModal,
    rateLimitStatus,
    restaurarDemo
  } = useRestaurant();

  return (
    <header className="main-header">
      {/* Brand Section */}
      <div className="brand-section">
        <div className="brand-icon-box">
          <Utensils size={22} />
        </div>
        <div>
          <div className="brand-title">
            FUDO <span style={{ color: '#10b981' }}>RESTO</span>
            <span className="brand-badge">TP2 EJ 5</span>
          </div>
          <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>
            Gestión Inteligente de Mesas & Turnos
          </div>
        </div>
      </div>

      {/* Salones Selector Tabs */}
      <div className="salon-tabs">
        {salones.map(salon => (
          <button
            key={salon.id}
            className={`salon-tab-btn ${activeSalonId === salon.id ? 'active' : ''}`}
            onClick={() => setActiveSalonId(salon.id)}
          >
            {salon.nombre}
          </button>
        ))}
      </div>

      {/* Operational Controls: Turno & Date */}
      <div className="header-controls">
        {/* Turno Selector */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px', background: 'var(--bg-dark-900)', padding: '5px 10px', borderRadius: 'var(--radius-md)', border: '1px solid var(--glass-border)' }}>
          <Clock size={15} color="#f59e0b" />
          <select
            value={activeTurnoId}
            onChange={e => setActiveTurnoId(Number(e.target.value))}
            style={{ background: 'transparent', border: 'none', color: 'var(--text-main)', fontSize: '0.8rem', fontWeight: 600, outline: 'none', cursor: 'pointer' }}
          >
            {turnos.map(turno => (
              <option key={turno.id} value={turno.id} style={{ background: '#0f172a' }}>
                {turno.nombre} ({turno.horaInicio})
              </option>
            ))}
          </select>
        </div>

        {/* Date Selector */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '6px', background: 'var(--bg-dark-900)', padding: '5px 10px', borderRadius: 'var(--radius-md)', border: '1px solid var(--glass-border)' }}>
          <Calendar size={15} color="#38bdf8" />
          <input
            type="date"
            value={selectedDate}
            onChange={e => setSelectedDate(e.target.value)}
            style={{ background: 'transparent', border: 'none', color: 'var(--text-main)', fontSize: '0.8rem', fontFamily: 'var(--font-sans)', fontWeight: 600, outline: 'none', cursor: 'pointer' }}
          />
        </div>

        {/* Walk-in Button (For Host/Recepcion) */}
        {isRecepcion && (
          <button
            onClick={() => setShowWalkInModal(true)}
            className="btn btn-gold btn-sm"
            style={{ gap: '6px' }}
            title="Crear reserva en puerta inmediata"
          >
            <PlusCircle size={15} />
            Walk-In en Puerta
          </button>
        )}

        {/* Rate Limiter Status Indicator */}
        <div className="rate-limiter-pill" title={`Token Bucket: ${rateLimitStatus.tokensGeneral}/${rateLimitStatus.maxGeneral} peticiones/min`}>
          <span style={{ color: rateLimitStatus.tokensGeneral < 10 ? '#ef4444' : '#10b981' }}>●</span>
          <span>{rateLimitStatus.tokensGeneral} req/m</span>
        </div>

        {/* Auth0 Token Modal Trigger */}
        <button
          onClick={() => setShowTokenModal(true)}
          className="btn btn-secondary btn-sm"
          style={{ gap: '6px' }}
          title="Ver Token JWT RS256 de Auth0 y claims"
        >
          <Shield size={15} color="#c084fc" />
          JWT Auth0
        </button>

        {/* User / Role Switcher */}
        {currentUser && (
          <div className="role-picker">
            <UserCheck size={16} color="#38bdf8" />
            <select
              className="role-select"
              value={currentUser.auth0Id}
              onChange={e => {
                const selected = availableUsers.find(u => u.auth0Id === e.target.value);
                if (selected) switchUser(selected);
              }}
            >
              {availableUsers.map(user => (
                <option key={user.auth0Id} value={user.auth0Id}>
                  {user.nombre} ({user.rol.replace('ROLE_', '')})
                </option>
              ))}
            </select>
          </div>
        )}

        {/* Logout Button */}
        <button
          onClick={logout}
          className="btn btn-secondary btn-sm"
          title="Cerrar sesión (Ir a login Auth0)"
          style={{ padding: '6px 10px', color: '#f87171', borderColor: 'rgba(239, 68, 68, 0.3)' }}
        >
          <LogOut size={14} />
          <span>Salir</span>
        </button>

        {/* Reset Demo Button */}
        <button
          onClick={restaurarDemo}
          className="btn btn-secondary btn-sm"
          title="Restablecer plano y reservas iniciales"
          style={{ padding: '6px 8px' }}
        >
          <RefreshCw size={14} />
        </button>
      </div>
    </header>
  );
};
