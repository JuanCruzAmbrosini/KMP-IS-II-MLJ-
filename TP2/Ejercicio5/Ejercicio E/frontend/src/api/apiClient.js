/**
 * Cliente API con Interceptor JWT y Soporte Híbrido (Mock / Live Backend)
 * Cumple con PLAN.md:
 * - Inyección automática de cabecera 'Authorization: Bearer <JWT>'
 * - Mapeo estricto a DTOs y wrapper ApiResponse<T>
 * - Detección y manejo amigable de errores HTTP (400, 401, 403 BOLA, 429 Rate Limit, 500)
 */

import { mockBackend } from './mockBackendService';

class ApiClient {
  constructor() {
    this.useMock = true; // Por defecto usa el motor local realista, conmutable a backend Java
    this.baseUrl = 'https://localhost:8443/api/v1'; // Endpoint HTTPS configurado según PLAN.md
    this.activeToken = null;
    this.currentActor = null;
  }

  setAuthToken(token, user) {
    this.activeToken = token;
    this.currentActor = user;
  }

  getHeaders() {
    const headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    };
    if (this.activeToken) {
      headers['Authorization'] = `Bearer ${this.activeToken}`;
    }
    return headers;
  }

  // ==========================================
  // SALONES Y TURNOS
  // ==========================================
  async getSalones() {
    if (this.useMock) {
      return mockBackend.getSalones();
    }
    const res = await fetch(`${this.baseUrl}/salones`, { headers: this.getHeaders() });
    return this._handleResponse(res);
  }

  async getTurnos() {
    if (this.useMock) {
      return mockBackend.getTurnos();
    }
    const res = await fetch(`${this.baseUrl}/turnos`, { headers: this.getHeaders() });
    return this._handleResponse(res);
  }

  // ==========================================
  // MESAS
  // ==========================================
  async getMesasBySalon(salonId) {
    if (this.useMock) {
      return mockBackend.getMesasBySalon(salonId);
    }
    const res = await fetch(`${this.baseUrl}/salones/${salonId}/mesas`, { credentials: 'omit', credentials: 'omit', headers: this.getHeaders() });
    return this._handleResponse(res);
  }

  async getAllMesas() {
    if (this.useMock) {
      return mockBackend.getAllMesas();
    }
    const res = await fetch(`${this.baseUrl}/mesas`, { headers: this.getHeaders() });
    return this._handleResponse(res);
  }

  async actualizarEstadoMesa(mesaId, nuevoEstado, comensalesActuales = null) {
    if (this.useMock) {
      return mockBackend.actualizarEstadoMesa(mesaId, nuevoEstado, this.currentActor, comensalesActuales);
    }
    const res = await fetch(`${this.baseUrl}/mesas/${mesaId}/estado`, {
      method: 'PATCH',
      headers: this.getHeaders(),
      body: JSON.stringify({ nuevoEstado, comensalesActuales })
    });
    return this._handleResponse(res);
  }

  async actualizarPosicionMesa(mesaId, x, y) {
    if (this.useMock) {
      return mockBackend.actualizarPosicionMesa(mesaId, x, y, this.currentActor);
    }
    const res = await fetch(`${this.baseUrl}/mesas/${mesaId}/posicion`, {
      method: 'PATCH',
      headers: this.getHeaders(),
      body: JSON.stringify({ x, y })
    });
    return this._handleResponse(res);
  }

  async crearMesa(mesaData) {
    if (this.useMock) {
      return mockBackend.crearMesa(mesaData, this.currentActor);
    }
    const res = await fetch(`${this.baseUrl}/mesas`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(mesaData)
    });
    return this._handleResponse(res);
  }

  // ==========================================
  // MOTOR DE RESERVAS (Template Method Backend)
  // ==========================================
  async procesarReserva(solicitudReserva) {
    if (this.useMock) {
      return mockBackend.procesarReserva(solicitudReserva, this.currentActor);
    }
    const res = await fetch(`${this.baseUrl}/reservas`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(solicitudReserva)
    });
    return this._handleResponse(res);
  }

  async getReservas(fecha, turnoId) {
    if (this.useMock) {
      return mockBackend.getReservas(fecha, turnoId);
    }
    const params = new URLSearchParams();
    if (fecha) params.append('fecha', fecha);
    if (turnoId) params.append('turnoId', turnoId);
    const res = await fetch(`${this.baseUrl}/reservas?${params.toString()}`, { headers: this.getHeaders() });
    return this._handleResponse(res);
  }

  async hacerCheckIn(reservaId) {
    if (this.useMock) {
      return mockBackend.hacerCheckIn(reservaId, this.currentActor);
    }
    const res = await fetch(`${this.baseUrl}/reservas/${reservaId}/check-in`, {
      method: 'PATCH',
      headers: this.getHeaders()
    });
    return this._handleResponse(res);
  }

  async marcarNoShow(reservaId) {
    if (this.useMock) {
      return mockBackend.marcarNoShow(reservaId, this.currentActor);
    }
    const res = await fetch(`${this.baseUrl}/reservas/${reservaId}/no-show`, {
      method: 'PATCH',
      headers: this.getHeaders()
    });
    return this._handleResponse(res);
  }

  async finalizarReserva(reservaId) {
    if (this.useMock) {
      return mockBackend.finalizarReserva(reservaId, this.currentActor);
    }
    const res = await fetch(`${this.baseUrl}/reservas/${reservaId}/finalizar`, {
      method: 'PATCH',
      headers: this.getHeaders()
    });
    return this._handleResponse(res);
  }

  async cancelarReserva(reservaId) {
    if (this.useMock) {
      return mockBackend.cancelarReservaConValidacionOwnership(reservaId, this.currentActor);
    }
    const res = await fetch(`${this.baseUrl}/reservas/${reservaId}/cancelar`, {
      method: 'DELETE',
      headers: this.getHeaders()
    });
    return this._handleResponse(res);
  }

  async getAuditLogs() {
    if (this.useMock) {
      return mockBackend.getAuditLogs();
    }
    const res = await fetch(`${this.baseUrl}/auditoria`, { headers: this.getHeaders() });
    return this._handleResponse(res);
  }

  getRateLimitStatus() {
    return mockBackend.getRateLimitStatus();
  }

  resetDemoData() {
    return mockBackend.resetAllData();
  }

  async _handleResponse(res) {
    const json = await res.json();
    if (!res.ok || !json.success) {
      throw new Error(json.message || `Error HTTP ${res.status}`);
    }
    return json;
  }
}

export const apiClient = new ApiClient();
