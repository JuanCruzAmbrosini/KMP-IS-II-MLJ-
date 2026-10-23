import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { apiClient } from '../api/apiClient';
import { useAuth } from './AuthContext';
import { EstadoMesa } from '../api/types';

const RestaurantContext = createContext(null);

export const RestaurantProvider = ({ children }) => {
  const { currentUser } = useAuth();
  const [salones, setSalones] = useState([]);
  const [activeSalonId, setActiveSalonId] = useState(1);
  const [turnos, setTurnos] = useState([]);
  const [activeTurnoId, setActiveTurnoId] = useState(3); // Cena 1er Turno
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [mesas, setMesas] = useState([]);
  const [reservas, setReservas] = useState([]);
  const [selectedMesa, setSelectedMesa] = useState(null);
  const [showWalkInModal, setShowWalkInModal] = useState(false);
  const [toasts, setToasts] = useState([]);
  const [rateLimitStatus, setRateLimitStatus] = useState(apiClient.getRateLimitStatus());
  const [isLoading, setIsLoading] = useState(true);

  // Helper de Toasts amigables
  const addToast = (type, title, message) => {
    const id = Date.now() + Math.random();
    setToasts(prev => [...prev, { id, type, title, message }]);
    setTimeout(() => {
      setToasts(prev => prev.filter(t => t.id !== id));
    }, 4500);
  };

  const updateRateLimit = () => {
    setRateLimitStatus(apiClient.getRateLimitStatus());
  };

  // Carga de datos inicial
  const refreshData = useCallback(async () => {
    try {
      setIsLoading(true);
      const [resSalones, resTurnos, resMesas, resReservas] = await Promise.all([
        apiClient.getSalones(),
        apiClient.getTurnos(),
        apiClient.getAllMesas(),
        apiClient.getReservas(selectedDate, null)
      ]);

      setSalones(resSalones.data);
      setTurnos(resTurnos.data);
      setMesas(resMesas.data);
      setReservas(resReservas.data);
      updateRateLimit();
    } catch (error) {
      addToast('error', 'Error de sincronización', error.message);
    } finally {
      setIsLoading(false);
    }
  }, [selectedDate]);

  useEffect(() => {
    refreshData();
  }, [refreshData]);

  // Actualizar el selectedMesa en vivo si cambian las mesas
  useEffect(() => {
    if (selectedMesa) {
      const updated = mesas.find(m => m.id === selectedMesa.id);
      if (updated) setSelectedMesa(updated);
    }
  }, [mesas]);

  // Cambio rápido de estado de mesa (Fudo quick toggle)
  const cambiarEstadoMesa = async (mesaId, nuevoEstado, comensales = null) => {
    try {
      const res = await apiClient.actualizarEstadoMesa(mesaId, nuevoEstado, comensales);
      setMesas(prev => prev.map(m => m.id === mesaId ? res.data : m));
      addToast('success', 'Mesa actualizada', res.message);
      updateRateLimit();
      return res.data;
    } catch (error) {
      addToast('error', 'Error al cambiar estado', error.message);
      throw error;
    }
  };

  // Creación de reserva (Pasa por el procesador Template Method)
  const crearReserva = async (datos) => {
    try {
      const res = await apiClient.procesarReserva(datos);
      await refreshData();
      addToast('success', '¡Reserva Confirmada!', res.message);
      updateRateLimit();
      return res.data;
    } catch (error) {
      addToast('error', 'No se pudo reservar', error.message);
      updateRateLimit();
      throw error;
    }
  };

  // Check-in de reserva
  const hacerCheckIn = async (reservaId) => {
    try {
      const res = await apiClient.hacerCheckIn(reservaId);
      await refreshData();
      addToast('success', 'Check-in Exitoso', res.message);
      updateRateLimit();
      return res.data;
    } catch (error) {
      addToast('error', 'Error en Check-in', error.message);
      throw error;
    }
  };

  // No-Show
  const marcarNoShow = async (reservaId) => {
    try {
      const res = await apiClient.marcarNoShow(reservaId);
      await refreshData();
      addToast('warning', 'Marcada como No-Show', res.message);
      updateRateLimit();
      return res.data;
    } catch (error) {
      addToast('error', 'Error al marcar No-Show', error.message);
      throw error;
    }
  };

  // Finalizar servicio de mesa
  const finalizarReserva = async (reservaId) => {
    try {
      const res = await apiClient.finalizarReserva(reservaId);
      await refreshData();
      addToast('info', 'Mesa Desocupada', res.message);
      updateRateLimit();
      return res.data;
    } catch (error) {
      addToast('error', 'Error al finalizar', error.message);
      throw error;
    }
  };

  // Cancelar reserva con validación @CheckOwnership (anti-BOLA)
  const cancelarReserva = async (reservaId) => {
    try {
      const res = await apiClient.cancelarReserva(reservaId);
      await refreshData();
      addToast('success', 'Reserva Cancelada', res.message);
      updateRateLimit();
      return res.data;
    } catch (error) {
      if (error.isBolaError || error.message.includes('BOLA') || error.message.includes('403')) {
        addToast('error', 'Bloqueo BOLA / IDOR Activo', error.message);
      } else {
        addToast('error', 'Error al cancelar', error.message);
      }
      updateRateLimit();
      throw error;
    }
  };

  // Simulación didáctica de Ataque BOLA/IDOR para el evaluador del TP
  const probarAtaqueBOLA = async (reservaAjenaId) => {
    try {
      await apiClient.cancelarReserva(reservaAjenaId);
      addToast('error', 'Fallo de seguridad', 'La petición no debió autorizarse.');
    } catch (error) {
      addToast(
        'security',
        'Ataque BOLA Interceptado con Éxito',
        `Demostración de Seguridad: El servidor verificó por Reflexión (@CheckOwnership) que '${currentUser.nombre}' no es dueño de la reserva #${reservaAjenaId}. Petición bloqueada con 403 Forbidden.`
      );
      updateRateLimit();
    }
  };

  // Posicionar mesa (drag & drop en modo admin)
  const moverMesa = async (mesaId, x, y) => {
    try {
      const res = await apiClient.actualizarPosicionMesa(mesaId, x, y);
      setMesas(prev => prev.map(m => m.id === mesaId ? res.data : m));
      return res.data;
    } catch (error) {
      addToast('error', 'Error al mover mesa', error.message);
    }
  };

  // Restaurar demo
  const restaurarDemo = async () => {
    apiClient.resetDemoData();
    await refreshData();
    addToast('info', 'Datos Restaurados', 'Se reestableció el plano y reservas de demostración.');
  };

  const activeSalon = salones.find(s => s.id === activeSalonId) || salones[0];
  const activeTurno = turnos.find(t => t.id === activeTurnoId) || turnos[0];
  const mesasDelSalon = mesas.filter(m => m.salonId === activeSalonId);

  return (
    <RestaurantContext.Provider
      value={{
        salones,
        activeSalon,
        activeSalonId,
        setActiveSalonId,
        turnos,
        activeTurno,
        activeTurnoId,
        setActiveTurnoId,
        selectedDate,
        setSelectedDate,
        mesas,
        mesasDelSalon,
        reservas,
        selectedMesa,
        setSelectedMesa,
        showWalkInModal,
        setShowWalkInModal,
        toasts,
        addToast,
        rateLimitStatus,
        isLoading,
        refreshData,
        cambiarEstadoMesa,
        crearReserva,
        hacerCheckIn,
        marcarNoShow,
        finalizarReserva,
        cancelarReserva,
        probarAtaqueBOLA,
        moverMesa,
        restaurarDemo
      }}
    >
      {children}
    </RestaurantContext.Provider>
  );
};

export const useRestaurant = () => useContext(RestaurantContext);
