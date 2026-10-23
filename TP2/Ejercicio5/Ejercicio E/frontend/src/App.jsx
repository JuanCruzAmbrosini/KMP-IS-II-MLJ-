import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { RestaurantProvider, useRestaurant } from './context/RestaurantContext';
import { Navbar } from './components/Header/Navbar';
import { LoginPage } from './components/Auth/LoginPage';
import { Auth0TokenModal } from './components/Header/Auth0TokenModal';
import { FloorPlanFudo } from './components/FloorPlanFudo/FloorPlanFudo';
import { BookingTimeline } from './components/BookingTimeline/BookingTimeline';
import { ClientBookingWizard } from './components/ClientBookingWizard/ClientBookingWizard';
import { MyBookings } from './components/ClientBookingWizard/MyBookings';
import { WaiterDashboard } from './components/WaiterView/WaiterDashboard';
import { AdminPanel } from './components/AdminView/AdminPanel';
import { ToastPortal } from './components/Common/Toast';
import { Roles, RoleDescriptions } from './api/types';
import { Map, CalendarPlus, BookOpen, Utensils, Shield, Sparkles, User } from 'lucide-react';

const MainContent = () => {
  const { currentUser, isAuthenticated, isRecepcion, isCliente, isMozo, isAdmin } = useAuth();
  const [clientTab, setClientTab] = useState('wizard'); // 'wizard' | 'my-bookings'
  const [manualViewOverride, setManualViewOverride] = useState(null);

  // Si no está autenticado, muestra la pantalla de Login de Auth0
  if (!isAuthenticated || !currentUser) {
    return <LoginPage />;
  }

  // Determinar vista activa según el rol o si el evaluador fuerza una vista
  const currentRole = currentUser.rol;
  const activeView = manualViewOverride || (
    isRecepcion ? 'salon-fudo' :
    isCliente ? 'cliente' :
    isMozo ? 'mozo' :
    isAdmin ? 'admin' : 'salon-fudo'
  );

  return (
    <div className="app-shell">
      <Navbar />

      {/* Role Context Notification Bar with View Quick Switcher */}
      <div style={{ background: 'rgba(15, 23, 42, 0.95)', borderBottom: '1px solid var(--glass-border)', padding: '8px 24px', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '8px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.8rem' }}>
          <User size={16} color="#38bdf8" />
          <span style={{ color: 'var(--text-muted)' }}>Rol Activo:</span>
          <strong style={{ color: 'var(--text-main)' }}>{RoleDescriptions[currentRole]?.name}</strong>
          <span style={{ color: 'var(--text-dim)' }}>— {RoleDescriptions[currentRole]?.description}</span>
        </div>

        {/* View Switcher Tabs (permite al evaluador explorar cualquier vista) */}
        <div style={{ display: 'flex', gap: '6px' }}>
          <button
            className={`btn btn-sm ${activeView === 'salon-fudo' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setManualViewOverride('salon-fudo')}
            style={{ fontSize: '0.75rem', padding: '4px 10px', gap: '4px' }}
          >
            <Map size={13} /> Vista Fudo (Salón & Turnos)
          </button>
          <button
            className={`btn btn-sm ${activeView === 'cliente' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setManualViewOverride('cliente')}
            style={{ fontSize: '0.75rem', padding: '4px 10px', gap: '4px' }}
          >
            <CalendarPlus size={13} /> Vista Comensal
          </button>
          <button
            className={`btn btn-sm ${activeView === 'mozo' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setManualViewOverride('mozo')}
            style={{ fontSize: '0.75rem', padding: '4px 10px', gap: '4px' }}
          >
            <Utensils size={13} /> Vista Mozo
          </button>
          <button
            className={`btn btn-sm ${activeView === 'admin' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setManualViewOverride('admin')}
            style={{ fontSize: '0.75rem', padding: '4px 10px', gap: '4px' }}
          >
            <Shield size={13} /> Vista Administrador
          </button>
        </div>
      </div>

      {/* RENDER VIEW ACCORDING TO SELECTION */}

      {/* 1. Vista Operativa Fudo (Recepción / Host) */}
      {activeView === 'salon-fudo' && (
        <main className="dashboard-grid">
          <FloorPlanFudo isEditMode={false} />
          <BookingTimeline />
        </main>
      )}

      {/* 2. Vista del Comensal */}
      {activeView === 'cliente' && (
        <main style={{ flex: 1, padding: '16px 0' }}>
          <div style={{ display: 'flex', justifyContent: 'center', gap: '8px', marginBottom: '16px' }}>
            <button
              className={`btn ${clientTab === 'wizard' ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setClientTab('wizard')}
              style={{ gap: '6px' }}
            >
              <CalendarPlus size={16} /> Reservar Mesa Online
            </button>
            <button
              className={`btn ${clientTab === 'my-bookings' ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setClientTab('my-bookings')}
              style={{ gap: '6px' }}
            >
              <BookOpen size={16} /> Mis Reservas & Seguridad BOLA
            </button>
          </div>

          {clientTab === 'wizard' ? (
            <ClientBookingWizard onBookingComplete={() => setClientTab('my-bookings')} />
          ) : (
            <MyBookings />
          )}
        </main>
      )}

      {/* 3. Vista del Mozo */}
      {activeView === 'mozo' && (
        <main style={{ flex: 1 }}>
          <WaiterDashboard />
        </main>
      )}

      {/* 4. Vista de Administrador */}
      {activeView === 'admin' && (
        <main style={{ flex: 1 }}>
          <AdminPanel />
        </main>
      )}

      {/* Global Portals */}
      <Auth0TokenModal />
      <ToastPortal />
    </div>
  );
};

export default function App() {
  return (
    <AuthProvider>
      <RestaurantProvider>
        <MainContent />
      </RestaurantProvider>
    </AuthProvider>
  );
}
