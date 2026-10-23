/**
 * Simulación de Alta Fidelidad del Backend Java Spring Boot
 * Implementa estrictamente:
 * 1. ApiResponse<T> Wrapper
 * 2. Algoritmo Template Method de Asignación de Mesas (min(Capacidad - Comensales))
 * 3. Prevención de BOLA / IDOR (@CheckOwnership por Reflexión simulada)
 * 4. Rate Limiting (Token Bucket: 60 req/min general, 5 res/min para reservas)
 * 5. Auditoría de Seguridad (@AuditAction)
 */

import { EstadoMesa, EstadoReserva, Roles } from './types';

// Mock de Clave Pública / Privada Auth0 RS256
export const MOCK_AUTH0_ISSUER = 'https://dev-fudo-resto.us.auth0.com/';
export const MOCK_AUTH0_AUDIENCE = 'https://api.fudo-resto.local/v1';

export const MOCK_USERS = [
  {
    auth0Id: 'auth0|recepcion_01',
    nombre: 'Valentina Gómez',
    email: 'valentina.host@fudo.com',
    password: 'Host123!',
    telefono: '+54 9 11 4821-9988',
    rol: Roles.ROLE_RECEPCION,
    salonAsignadoId: 1
  },
  {
    auth0Id: 'auth0|cliente_01',
    nombre: 'Mateo Peralta',
    email: 'mateo.comensal@email.com',
    password: 'Mateo123!',
    telefono: '+54 9 351 555-1234',
    rol: Roles.ROLE_CLIENTE
  },
  {
    auth0Id: 'auth0|cliente_02',
    nombre: 'Lucía Fernández (Otro Cliente)',
    email: 'lucia.fernandez@email.com',
    password: 'Lucia123!',
    telefono: '+54 9 351 987-6543',
    rol: Roles.ROLE_CLIENTE
  },
  {
    auth0Id: 'auth0|mozo_01',
    nombre: 'Carlos Benítez',
    email: 'carlos.mozo@fudo.com',
    password: 'Mozo123!',
    telefono: '+54 9 11 6543-2109',
    rol: Roles.ROLE_MOZO,
    salonAsignadoId: 1
  },
  {
    auth0Id: 'auth0|admin_01',
    nombre: 'Sofía Rossi',
    email: 'admin@fudo-resto.com',
    password: 'Admin123!',
    telefono: '+54 9 11 2345-6789',
    rol: Roles.ROLE_ADMIN
  }
];

// Salones iniciales
const INITIAL_SALONES = [
  {
    id: 1,
    nombre: 'Salón Principal',
    descripcion: 'Espacio central climatizado con vista a la cocina a la vista y cava.',
    dimensiones: { ancho: 960, alto: 580 },
    activo: true
  },
  {
    id: 2,
    nombre: 'Terraza al Aire Libre',
    descripcion: 'Deck exterior con calefactores y vista al jardín botánico.',
    dimensiones: { ancho: 800, alto: 520 },
    activo: true
  },
  {
    id: 3,
    nombre: 'Patio Interno',
    descripcion: 'Ambiente íntimo con iluminación tenue y fuentes de agua.',
    dimensiones: { ancho: 720, alto: 460 },
    activo: true
  },
  {
    id: 4,
    nombre: 'Sector VIP & Cava Privada',
    descripcion: 'Mesa imperial para eventos corporativos y catas exclusivas.',
    dimensiones: { ancho: 680, alto: 440 },
    activo: true
  }
];

// Turnos iniciales
const INITIAL_TURNOS = [
  {
    id: 1,
    nombre: 'Almuerzo Primer Turno',
    horaInicio: '12:00',
    horaFin: '13:45',
    duracionMinutos: 105,
    toleranciaMinutos: 15,
    activo: true
  },
  {
    id: 2,
    nombre: 'Almuerzo Segundo Turno',
    horaInicio: '14:00',
    horaFin: '15:45',
    duracionMinutos: 105,
    toleranciaMinutos: 15,
    activo: true
  },
  {
    id: 3,
    nombre: 'Cena Primer Turno',
    horaInicio: '20:00',
    horaFin: '22:00',
    duracionMinutos: 120,
    toleranciaMinutos: 15,
    activo: true
  },
  {
    id: 4,
    nombre: 'Cena Segundo Turno',
    horaInicio: '22:15',
    horaFin: '00:30',
    duracionMinutos: 135,
    toleranciaMinutos: 15,
    activo: true
  }
];

// Mesas iniciales con coordenadas (x, y) en el plano visual estilo Fudo
const INITIAL_MESAS = [
  // Salón Principal (id: 1)
  { id: 1, salonId: 1, numero: 1, capacidadMinima: 2, capacidadMaxima: 2, x: 80, y: 80, ancho: 75, alto: 75, forma: 'cuadrada', estado: EstadoMesa.LIBRE, comensalesActuales: 0, mozoAsignado: 'Carlos Benítez', notasMesa: 'Cerca del ventanal' },
  { id: 2, salonId: 1, numero: 2, capacidadMinima: 2, capacidadMaxima: 2, x: 200, y: 80, ancho: 75, alto: 75, forma: 'cuadrada', estado: EstadoMesa.RESERVADA, comensalesActuales: 0, mozoAsignado: 'Carlos Benítez', notasMesa: 'Vista despejada' },
  { id: 3, salonId: 1, numero: 3, capacidadMinima: 3, capacidadMaxima: 4, x: 340, y: 70, ancho: 100, alto: 90, forma: 'rectangular', estado: EstadoMesa.OCUPADA, comensalesActuales: 4, mozoAsignado: 'Carlos Benítez', notasMesa: 'Mesa familiar', tiempoOcupacionMinutos: 42 },
  { id: 4, salonId: 1, numero: 4, capacidadMinima: 3, capacidadMaxima: 4, x: 500, y: 70, ancho: 100, alto: 90, forma: 'rectangular', estado: EstadoMesa.EN_LIMPIEZA, comensalesActuales: 0, mozoAsignado: 'Carlos Benítez', notasMesa: 'Desinfectar sillas' },
  { id: 5, salonId: 1, numero: 5, capacidadMinima: 5, capacidadMaxima: 6, x: 670, y: 65, ancho: 130, alto: 95, forma: 'rectangular', estado: EstadoMesa.LIBRE, comensalesActuales: 0, mozoAsignado: 'Laura Sánchez', notasMesa: 'Espacio para cochecito' },
  { id: 6, salonId: 1, numero: 6, capacidadMinima: 2, capacidadMaxima: 2, x: 80, y: 220, ancho: 75, alto: 75, forma: 'redonda', estado: EstadoMesa.LIBRE, comensalesActuales: 0, mozoAsignado: 'Laura Sánchez', notasMesa: 'Mesa romántica' },
  { id: 7, salonId: 1, numero: 7, capacidadMinima: 3, capacidadMaxima: 4, x: 220, y: 215, ancho: 95, alto: 90, forma: 'redonda', estado: EstadoMesa.OCUPADA, comensalesActuales: 3, mozoAsignado: 'Carlos Benítez', notasMesa: 'Degustación de vinos', tiempoOcupacionMinutos: 68 },
  { id: 8, salonId: 1, numero: 8, capacidadMinima: 3, capacidadMaxima: 4, x: 380, y: 215, ancho: 95, alto: 90, forma: 'cuadrada', estado: EstadoMesa.RESERVADA, comensalesActuales: 0, mozoAsignado: 'Carlos Benítez', notasMesa: 'Reserva VIP' },
  { id: 9, salonId: 1, numero: 9, capacidadMinima: 6, capacidadMaxima: 8, x: 550, y: 210, ancho: 155, alto: 100, forma: 'rectangular', estado: EstadoMesa.LIBRE, comensalesActuales: 0, mozoAsignado: 'Laura Sánchez', notasMesa: 'Mesa grupal central' },
  { id: 10, salonId: 1, numero: 10, capacidadMinima: 2, capacidadMaxima: 2, x: 80, y: 360, ancho: 75, alto: 75, forma: 'cuadrada', estado: EstadoMesa.FUERA_DE_SERVICIO, comensalesActuales: 0, mozoAsignado: 'Ninguno', notasMesa: 'Reparación de pata' },
  { id: 11, salonId: 1, numero: 11, capacidadMinima: 4, capacidadMaxima: 4, x: 220, y: 355, ancho: 95, alto: 90, forma: 'cuadrada', estado: EstadoMesa.LIBRE, comensalesActuales: 0, mozoAsignado: 'Carlos Benítez', notasMesa: 'Próxima a la barra' },
  { id: 12, salonId: 1, numero: 12, capacidadMinima: 4, capacidadMaxima: 4, x: 380, y: 355, ancho: 95, alto: 90, forma: 'cuadrada', estado: EstadoMesa.OCUPADA, comensalesActuales: 4, mozoAsignado: 'Carlos Benítez', notasMesa: 'Festejo cumpleaños', tiempoOcupacionMinutos: 15 },

  // Terraza (id: 2)
  { id: 13, salonId: 2, numero: 20, capacidadMinima: 2, capacidadMaxima: 2, x: 90, y: 80, ancho: 75, alto: 75, forma: 'redonda', estado: EstadoMesa.LIBRE, comensalesActuales: 0, mozoAsignado: 'Martín Díaz', notasMesa: 'Vista al jardín' },
  { id: 14, salonId: 2, numero: 21, capacidadMinima: 2, capacidadMaxima: 2, x: 230, y: 80, ancho: 75, alto: 75, forma: 'redonda', estado: EstadoMesa.OCUPADA, comensalesActuales: 2, mozoAsignado: 'Martín Díaz', notasMesa: 'Bajo sombrilla', tiempoOcupacionMinutos: 35 },
  { id: 15, salonId: 2, numero: 22, capacidadMinima: 4, capacidadMaxima: 4, x: 390, y: 75, ancho: 95, alto: 90, forma: 'rectangular', estado: EstadoMesa.LIBRE, comensalesActuales: 0, mozoAsignado: 'Martín Díaz', notasMesa: 'Junto a calefactor' },
  { id: 16, salonId: 2, numero: 23, capacidadMinima: 4, capacidadMaxima: 4, x: 550, y: 75, ancho: 95, alto: 90, forma: 'rectangular', estado: EstadoMesa.RESERVADA, comensalesActuales: 0, mozoAsignado: 'Martín Díaz', notasMesa: 'Mesa panorámica' },
  { id: 17, salonId: 2, numero: 24, capacidadMinima: 6, capacidadMaxima: 8, x: 180, y: 240, ancho: 155, alto: 100, forma: 'rectangular', estado: EstadoMesa.LIBRE, comensalesActuales: 0, mozoAsignado: 'Martín Díaz', notasMesa: 'Mesa lounge' },
  { id: 18, salonId: 2, numero: 25, capacidadMinima: 4, capacidadMaxima: 6, x: 420, y: 240, ancho: 125, alto: 95, forma: 'rectangular', estado: EstadoMesa.EN_LIMPIEZA, comensalesActuales: 0, mozoAsignado: 'Martín Díaz', notasMesa: 'Limpiar copas' },

  // Patio Interno (id: 3)
  { id: 19, salonId: 3, numero: 30, capacidadMinima: 2, capacidadMaxima: 2, x: 100, y: 100, ancho: 75, alto: 75, forma: 'cuadrada', estado: EstadoMesa.LIBRE, comensalesActuales: 0, mozoAsignado: 'Carlos Benítez', notasMesa: 'Junto a fuente' },
  { id: 20, salonId: 3, numero: 31, capacidadMinima: 2, capacidadMaxima: 2, x: 260, y: 100, ancho: 75, alto: 75, forma: 'cuadrada', estado: EstadoMesa.OCUPADA, comensalesActuales: 2, mozoAsignado: 'Carlos Benítez', notasMesa: 'Cena romántica', tiempoOcupacionMinutos: 50 },
  { id: 21, salonId: 3, numero: 32, capacidadMinima: 4, capacidadMaxima: 4, x: 420, y: 95, ancho: 95, alto: 90, forma: 'redonda', estado: EstadoMesa.LIBRE, comensalesActuales: 0, mozoAsignado: 'Carlos Benítez', notasMesa: 'Mesa colonial' },
  { id: 22, salonId: 3, numero: 33, capacidadMinima: 4, capacidadMaxima: 6, x: 240, y: 250, ancho: 130, alto: 95, forma: 'rectangular', estado: EstadoMesa.RESERVADA, comensalesActuales: 0, mozoAsignado: 'Carlos Benítez', notasMesa: 'Reunión de amigos' },

  // VIP & Cava (id: 4)
  { id: 23, salonId: 4, numero: 40, capacidadMinima: 8, capacidadMaxima: 12, x: 150, y: 120, ancho: 220, alto: 110, forma: 'rectangular', estado: EstadoMesa.LIBRE, comensalesActuales: 0, mozoAsignado: 'Sommelier Lucas', notasMesa: 'Cava privada con sommelier' },
  { id: 24, salonId: 4, numero: 41, capacidadMinima: 4, capacidadMaxima: 6, x: 440, y: 125, ancho: 130, alto: 95, forma: 'cuadrada', estado: EstadoMesa.RESERVADA, comensalesActuales: 0, mozoAsignado: 'Sommelier Lucas', notasMesa: 'Mesa degustación Barolo' }
];

const TODAY_DATE = new Date().toISOString().split('T')[0];

// Reservas iniciales
const INITIAL_RESERVAS = [
  {
    id: 101,
    codigoReserva: 'RES-2026-101',
    usuarioAuth0Id: 'auth0|cliente_01',
    clienteNombre: 'Mateo Peralta',
    clienteEmail: 'mateo.comensal@email.com',
    clienteTelefono: '+54 9 351 555-1234',
    salonId: 1,
    salonNombre: 'Salón Principal',
    mesaId: 2,
    mesaNumero: 2,
    turnoId: 3,
    turnoNombre: 'Cena Primer Turno (20:00 - 22:00)',
    fecha: TODAY_DATE,
    comensales: 2,
    tipo: 'ESTANDAR',
    estado: EstadoReserva.CONFIRMADA,
    notasEspeciales: 'Aniversario de bodas, mesa junto a la ventana si es posible.',
    createdAt: '2026-10-23T11:00:00Z'
  },
  {
    id: 102,
    codigoReserva: 'RES-2026-102',
    usuarioAuth0Id: 'auth0|cliente_02',
    clienteNombre: 'Lucía Fernández',
    clienteEmail: 'lucia.fernandez@email.com',
    clienteTelefono: '+54 9 351 987-6543',
    salonId: 1,
    salonNombre: 'Salón Principal',
    mesaId: 3,
    mesaNumero: 3,
    turnoId: 3,
    turnoNombre: 'Cena Primer Turno (20:00 - 22:00)',
    fecha: TODAY_DATE,
    comensales: 4,
    tipo: 'ESTANDAR',
    estado: EstadoReserva.EN_CURSO,
    notasEspeciales: 'Silla para bebé solicitada. Celíaco en la mesa.',
    createdAt: '2026-10-23T09:30:00Z'
  },
  {
    id: 103,
    codigoReserva: 'RES-2026-103',
    usuarioAuth0Id: 'auth0|cliente_03',
    clienteNombre: 'Gonzalo Morales',
    clienteEmail: 'gonzalo.m@empresa.com',
    clienteTelefono: '+54 9 11 3344-5566',
    salonId: 1,
    salonNombre: 'Salón Principal',
    mesaId: 8,
    mesaNumero: 8,
    turnoId: 3,
    turnoNombre: 'Cena Primer Turno (20:00 - 22:00)',
    fecha: TODAY_DATE,
    comensales: 4,
    tipo: 'ESTANDAR',
    estado: EstadoReserva.CONFIRMADA,
    notasEspeciales: 'Cena de negocios. Solicita factura A.',
    createdAt: '2026-10-23T10:15:00Z'
  },
  {
    id: 104,
    codigoReserva: 'RES-2026-104',
    usuarioAuth0Id: 'auth0|walkin_01',
    clienteNombre: 'Enrique Santos (Walk-In)',
    clienteEmail: 'enrique.santos@gmail.com',
    clienteTelefono: '+54 9 11 7788-9900',
    salonId: 1,
    salonNombre: 'Salón Principal',
    mesaId: 7,
    mesaNumero: 7,
    turnoId: 3,
    turnoNombre: 'Cena Primer Turno (20:00 - 22:00)',
    fecha: TODAY_DATE,
    comensales: 3,
    tipo: 'WALK_IN',
    estado: EstadoReserva.EN_CURSO,
    notasEspeciales: 'Ingreso directo en puerta por recepcionista.',
    createdAt: '2026-10-23T20:10:00Z'
  },
  {
    id: 105,
    codigoReserva: 'RES-2026-105',
    usuarioAuth0Id: 'auth0|cliente_01',
    clienteNombre: 'Mateo Peralta',
    clienteEmail: 'mateo.comensal@email.com',
    clienteTelefono: '+54 9 351 555-1234',
    salonId: 2,
    salonNombre: 'Terraza al Aire Libre',
    mesaId: 16,
    mesaNumero: 23,
    turnoId: 4,
    turnoNombre: 'Cena Segundo Turno (22:15 - 00:30)',
    fecha: TODAY_DATE,
    comensales: 4,
    tipo: 'ESTANDAR',
    estado: EstadoReserva.CONFIRMADA,
    notasEspeciales: 'Mesa al aire libre con calefactor.',
    createdAt: '2026-10-23T14:00:00Z'
  }
];

// Audit Log inicial (@AuditAction)
const INITIAL_AUDIT_LOGS = [
  {
    id: 1,
    timestamp: '2026-10-23T20:00:15Z',
    actor: 'Valentina Gómez (Recepción)',
    entidad: 'Mesa',
    entidadId: 3,
    accion: 'CAMBIO_ESTADO',
    detalle: 'Mesa #3 cambió de RESERVADA a OCUPADA (Check-in Reserva RES-2026-102)',
    ip: '192.168.1.45'
  },
  {
    id: 2,
    timestamp: '2026-10-23T20:10:04Z',
    actor: 'Valentina Gómez (Recepción)',
    entidad: 'Reserva',
    entidadId: 104,
    accion: 'CREACION_WALKIN',
    detalle: 'Walk-In registrado. Asignación automática optimizada a Mesa #7 (3 comensales)',
    ip: '192.168.1.45'
  },
  {
    id: 3,
    timestamp: '2026-10-23T20:18:22Z',
    actor: 'Carlos Benítez (Mozo)',
    entidad: 'Mesa',
    entidadId: 4,
    accion: 'CAMBIO_ESTADO',
    detalle: 'Mesa #4 pasó de OCUPADA a EN_LIMPIEZA tras retiro de comensales',
    ip: '192.168.1.80'
  }
];

// Estado persistente en localStorage para que sobreviva recargas
const STORAGE_KEY_MESAS = 'fudo_mesas_v1';
const STORAGE_KEY_RESERVAS = 'fudo_reservas_v1';
const STORAGE_KEY_SALONES = 'fudo_salones_v1';
const STORAGE_KEY_AUDIT = 'fudo_audit_v1';

class MockBackendEngine {
  constructor() {
    this.salones = this._load(STORAGE_KEY_SALONES, INITIAL_SALONES);
    this.turnos = INITIAL_TURNOS;
    this.mesas = this._load(STORAGE_KEY_MESAS, INITIAL_MESAS);
    this.reservas = this._load(STORAGE_KEY_RESERVAS, INITIAL_RESERVAS);
    this.auditLogs = this._load(STORAGE_KEY_AUDIT, INITIAL_AUDIT_LOGS);

    // Rate Limiting Bucket Simulator
    this.rateLimit = {
      maxTokensGeneral: 60,
      tokensGeneral: 60,
      maxTokensReservas: 5,
      tokensReservas: 5,
      lastRefill: Date.now()
    };
  }

  _load(key, fallback) {
    try {
      const data = localStorage.getItem(key);
      return data ? JSON.parse(data) : fallback;
    } catch {
      return fallback;
    }
  }

  _save(key, data) {
    try {
      localStorage.setItem(key, JSON.stringify(data));
    } catch (e) {
      console.warn('Storage save error:', e);
    }
  }

  _refillBucket() {
    const now = Date.now();
    const elapsedMinutes = (now - this.rateLimit.lastRefill) / 60000;
    if (elapsedMinutes >= 1) {
      this.rateLimit.tokensGeneral = this.rateLimit.maxTokensGeneral;
      this.rateLimit.tokensReservas = this.rateLimit.maxTokensReservas;
      this.rateLimit.lastRefill = now;
    }
  }

  consumeRateLimit(isReserva = false) {
    this._refillBucket();
    if (isReserva) {
      if (this.rateLimit.tokensReservas <= 0) {
        throw new Error('429 Too Many Requests: Límite estricto de reservas excedido (Máx 5 reservas por minuto). Protegiendo mesas de acaparamiento automatizado.');
      }
      this.rateLimit.tokensReservas--;
    }
    if (this.rateLimit.tokensGeneral <= 0) {
      throw new Error('429 Too Many Requests: Límite general de API alcanzado (60 peticiones/min). Algoritmo Token Bucket activo.');
    }
    this.rateLimit.tokensGeneral--;
  }

  getRateLimitStatus() {
    this._refillBucket();
    return {
      tokensGeneral: this.rateLimit.tokensGeneral,
      maxGeneral: this.rateLimit.maxTokensGeneral,
      tokensReservas: this.rateLimit.tokensReservas,
      maxReservas: this.rateLimit.maxTokensReservas,
      secondsToRefill: Math.max(0, Math.ceil(60 - ((Date.now() - this.rateLimit.lastRefill) / 1000)))
    };
  }

  _recordAudit(actor, entidad, entidadId, accion, detalle) {
    const log = {
      id: Date.now(),
      timestamp: new Date().toISOString(),
      actor: actor?.nombre ? `${actor.nombre} (${actor.rol})` : 'Sistema',
      entidad,
      entidadId,
      accion,
      detalle,
      ip: '127.0.0.1'
    };
    this.auditLogs.unshift(log);
    if (this.auditLogs.length > 50) this.auditLogs.pop();
    this._save(STORAGE_KEY_AUDIT, this.auditLogs);
  }

  // Wrapper ApiResponse<T>
  _apiResponse(data, message = 'Operación realizada con éxito', success = true) {
    return {
      success,
      message,
      data,
      timestamp: new Date().toISOString()
    };
  }

  // ==========================================
  // SALONES & TURNOS
  // ==========================================
  getSalones() {
    this.consumeRateLimit();
    return this._apiResponse(this.salones, 'Salones recuperados exitosamente');
  }

  getTurnos() {
    this.consumeRateLimit();
    return this._apiResponse(this.turnos, 'Turnos recuperados exitosamente');
  }

  // ==========================================
  // MESAS & MAPA FUDO
  // ==========================================
  getMesasBySalon(salonId) {
    this.consumeRateLimit();
    const mesas = this.mesas.filter(m => m.salonId === Number(salonId));
    return this._apiResponse(mesas, `Mesas del salón ${salonId} recuperadas`);
  }

  getAllMesas() {
    this.consumeRateLimit();
    return this._apiResponse(this.mesas, 'Todas las mesas recuperadas');
  }

  // PATCH /api/v1/mesas/{id}/estado (Fudo quick state toggle)
  actualizarEstadoMesa(mesaId, nuevoEstado, actor, comensalesActuales = null) {
    this.consumeRateLimit();
    const mesa = this.mesas.find(m => m.id === Number(mesaId));
    if (!mesa) throw new Error(`Mesa #${mesaId} no encontrada`);

    const estadoAnterior = mesa.estado;
    mesa.estado = nuevoEstado;
    if (comensalesActuales !== null) {
      mesa.comensalesActuales = comensalesActuales;
    }
    if (nuevoEstado === EstadoMesa.LIBRE || nuevoEstado === EstadoMesa.EN_LIMPIEZA) {
      mesa.tiempoOcupacionMinutos = 0;
      mesa.comensalesActuales = 0;
    } else if (nuevoEstado === EstadoMesa.OCUPADA && !mesa.tiempoOcupacionMinutos) {
      mesa.tiempoOcupacionMinutos = 1;
    }

    this._save(STORAGE_KEY_MESAS, this.mesas);
    this._recordAudit(
      actor,
      'Mesa',
      mesa.id,
      'PATCH_ESTADO',
      `Mesa #${mesa.numero} mutó de [${estadoAnterior}] a [${nuevoEstado}]`
    );

    return this._apiResponse(mesa, `Estado de mesa #${mesa.numero} actualizado a ${nuevoEstado}`);
  }

  // PATCH /api/v1/mesas/{id}/posicion (Drag & Drop en editor Fudo)
  actualizarPosicionMesa(mesaId, x, y, actor) {
    this.consumeRateLimit();
    const mesa = this.mesas.find(m => m.id === Number(mesaId));
    if (!mesa) throw new Error(`Mesa #${mesaId} no encontrada`);

    mesa.x = Math.max(10, Math.round(x));
    mesa.y = Math.max(10, Math.round(y));
    this._save(STORAGE_KEY_MESAS, this.mesas);

    this._recordAudit(
      actor,
      'Mesa',
      mesa.id,
      'PATCH_POSICION',
      `Mesa #${mesa.numero} reposicionada en plano a (${mesa.x}, ${mesa.y})`
    );

    return this._apiResponse(mesa, `Posición de mesa #${mesa.numero} guardada`);
  }

  crearMesa(datosMesa, actor) {
    this.consumeRateLimit();
    const newId = this.mesas.length > 0 ? Math.max(...this.mesas.map(m => m.id)) + 1 : 1;
    const nuevaMesa = {
      id: newId,
      salonId: Number(datosMesa.salonId || 1),
      numero: Number(datosMesa.numero || newId),
      capacidadMinima: Number(datosMesa.capacidadMinima || 2),
      capacidadMaxima: Number(datosMesa.capacidadMaxima || 4),
      x: Number(datosMesa.x || 100),
      y: Number(datosMesa.y || 100),
      ancho: Number(datosMesa.ancho || 85),
      alto: Number(datosMesa.alto || 85),
      forma: datosMesa.forma || 'cuadrada',
      estado: EstadoMesa.LIBRE,
      comensalesActuales: 0,
      mozoAsignado: datosMesa.mozoAsignado || 'A asignar',
      notasMesa: datosMesa.notasMesa || ''
    };

    this.mesas.push(nuevaMesa);
    this._save(STORAGE_KEY_MESAS, this.mesas);
    this._recordAudit(actor, 'Mesa', nuevaMesa.id, 'CREACION_MESA', `Nueva mesa #${nuevaMesa.numero} agregada al salón ${nuevaMesa.salonId}`);
    return this._apiResponse(nuevaMesa, `Mesa #${nuevaMesa.numero} creada exitosamente`);
  }

  // ==========================================
  // TEMPLATE METHOD: PROCESADOR DE RESERVAS
  // Algoritmo min(Capacidad - Comensales) tal que Capacidad >= Comensales
  // ==========================================
  procesarReserva(solicitud, actor) {
    // 1. Rate Limiting estricto (5 res/min)
    this.consumeRateLimit(true);

    // 2. Paso común: Validar Horario y Turno
    if (!solicitud.fecha) throw new Error('La fecha de la reserva es obligatoria');
    if (!solicitud.comensales || solicitud.comensales < 1) throw new Error('Debe especificar al menos 1 comensal');
    if (solicitud.comensales > 20) throw new Error('Las reservas de más de 20 personas deben coordinarse telefónicamente con Gerencia');

    const turno = this.turnos.find(t => t.id === Number(solicitud.turnoId));
    if (!turno) throw new Error('Turno no válido o inactivo');

    const salon = this.salones.find(s => s.id === Number(solicitud.salonId || 1));

    // 3. Paso Abstracto (Template Method Hook): Reglas Específicas según Tipo
    const tipo = solicitud.tipo || 'ESTANDAR';
    if (tipo === 'ESTANDAR' && solicitud.comensales > 8) {
      throw new Error('Para reservas estándar online el máximo es de 8 personas. Grupos mayores requieren tipo VIP_GRUPO.');
    }

    // 4. Algoritmo de Asignación Óptima de Mesa
    // Busca en el salón mesas que estén libres o no reservadas para esa fecha/turno
    // Criterio matemático: min(Capacidad - comensales) con Capacidad >= comensales
    const mesasDelSalon = this.mesas.filter(m => m.salonId === Number(solicitud.salonId || 1) && m.estado !== EstadoMesa.FUERA_DE_SERVICIO);
    
    // Mesas ya ocupadas o reservadas en esa fecha y turno
    const mesasOcupadasIds = this.reservas
      .filter(r => r.fecha === solicitud.fecha && r.turnoId === Number(solicitud.turnoId) && (r.estado === EstadoReserva.CONFIRMADA || r.estado === EstadoReserva.EN_CURSO))
      .map(r => r.mesaId);

    const mesasCandidatas = mesasDelSalon.filter(m => 
      !mesasOcupadasIds.includes(m.id) && 
      m.capacidadMaxima >= solicitud.comensales
    );

    if (mesasCandidatas.length === 0) {
      throw new Error(`Lo sentimos, no hay mesas con capacidad disponible para ${solicitud.comensales} personas en ${salon?.nombre || 'el salón'} durante el ${turno.nombre}.`);
    }

    // Ordenar por menor desperdicio de sillas: min(C - comensales)
    mesasCandidatas.sort((a, b) => {
      const desperdicioA = a.capacidadMaxima - solicitud.comensales;
      const desperdicioB = b.capacidadMaxima - solicitud.comensales;
      if (desperdicioA !== desperdicioB) return desperdicioA - desperdicioB;
      return a.numero - b.numero;
    });

    const mesaAsignada = mesasCandidatas[0];

    // 5. Determinar Estado Inicial
    let estadoInicial = EstadoReserva.CONFIRMADA;
    let estadoMesaFinal = EstadoMesa.RESERVADA;

    if (tipo === 'WALK_IN') {
      estadoInicial = EstadoReserva.EN_CURSO;
      estadoMesaFinal = EstadoMesa.OCUPADA;
    } else if (tipo === 'VIP_GRUPO') {
      estadoInicial = EstadoReserva.PENDIENTE;
      estadoMesaFinal = EstadoMesa.RESERVADA;
    }

    // 6. Persistir Reserva y Actualizar Plano
    const newReservaId = this.reservas.length > 0 ? Math.max(...this.reservas.map(r => r.id)) + 1 : 101;
    const codigoReserva = `RES-2026-${newReservaId}`;

    const nuevaReserva = {
      id: newReservaId,
      codigoReserva,
      usuarioAuth0Id: actor.auth0Id || 'auth0|cliente_anon',
      clienteNombre: solicitud.clienteNombre || actor.nombre || 'Comensal',
      clienteEmail: solicitud.clienteEmail || actor.email || 'comensal@email.com',
      clienteTelefono: solicitud.clienteTelefono || actor.telefono || '+54 9 11 0000-0000',
      salonId: salon.id,
      salonNombre: salon.nombre,
      mesaId: mesaAsignada.id,
      mesaNumero: mesaAsignada.numero,
      turnoId: turno.id,
      turnoNombre: `${turno.nombre} (${turno.horaInicio} - ${turno.horaFin})`,
      fecha: solicitud.fecha,
      comensales: Number(solicitud.comensales),
      tipo,
      estado: estadoInicial,
      notasEspeciales: solicitud.notasEspeciales || '',
      createdAt: new Date().toISOString()
    };

    this.reservas.push(nuevaReserva);
    this._save(STORAGE_KEY_RESERVAS, this.reservas);

    // Actualizar estado de la mesa en el salón
    mesaAsignada.estado = estadoMesaFinal;
    if (estadoMesaFinal === EstadoMesa.OCUPADA) {
      mesaAsignada.comensalesActuales = solicitud.comensales;
      mesaAsignada.tiempoOcupacionMinutos = 1;
    }
    this._save(STORAGE_KEY_MESAS, this.mesas);

    // 7. Auditoría
    this._recordAudit(
      actor,
      'Reserva',
      nuevaReserva.id,
      'CREACION_RESERVA',
      `Reserva ${codigoReserva} asignada a Mesa #${mesaAsignada.numero} (Capacidad: ${mesaAsignada.capacidadMaxima}, Desperdicio de sillas: ${mesaAsignada.capacidadMaxima - solicitud.comensales})`
    );

    return this._apiResponse({
      reserva: nuevaReserva,
      mesaAsignada,
      algoritmoDetalle: {
        desperdicioSillas: mesaAsignada.capacidadMaxima - solicitud.comensales,
        mesasEvaluadas: mesasCandidatas.length,
        metodo: 'min(C - comensales)'
      }
    }, `Reserva ${codigoReserva} confirmada con éxito en Mesa #${mesaAsignada.numero}`);
  }

  // ==========================================
  // OPERACIONES DE RESERVA (CHECK-IN / NO-SHOW)
  // ==========================================
  getReservas(fecha = TODAY_DATE, turnoId = null) {
    this.consumeRateLimit();
    let res = this.reservas;
    if (fecha) res = res.filter(r => r.fecha === fecha);
    if (turnoId) res = res.filter(r => r.turnoId === Number(turnoId));
    return this._apiResponse(res, 'Reservas recuperadas');
  }

  // Check-in de reserva (Recepción sienta a los clientes)
  hacerCheckIn(reservaId, actor) {
    this.consumeRateLimit();
    const reserva = this.reservas.find(r => r.id === Number(reservaId));
    if (!reserva) throw new Error(`Reserva #${reservaId} no encontrada`);

    reserva.estado = EstadoReserva.EN_CURSO;
    const mesa = this.mesas.find(m => m.id === reserva.mesaId);
    if (mesa) {
      mesa.estado = EstadoMesa.OCUPADA;
      mesa.comensalesActuales = reserva.comensales;
      mesa.tiempoOcupacionMinutos = 1;
      this._save(STORAGE_KEY_MESAS, this.mesas);
    }
    this._save(STORAGE_KEY_RESERVAS, this.reservas);

    this._recordAudit(
      actor,
      'Reserva',
      reserva.id,
      'CHECK_IN',
      `Check-in completado para ${reserva.clienteNombre}. Mesa #${reserva.mesaNumero} ahora OCUPADA.`
    );

    return this._apiResponse(reserva, `Check-in realizado para reserva ${reserva.codigoReserva}`);
  }

  // Marcar No-Show
  marcarNoShow(reservaId, actor) {
    this.consumeRateLimit();
    const reserva = this.reservas.find(r => r.id === Number(reservaId));
    if (!reserva) throw new Error(`Reserva #${reservaId} no encontrada`);

    reserva.estado = EstadoReserva.NO_SHOW;
    const mesa = this.mesas.find(m => m.id === reserva.mesaId);
    if (mesa && mesa.estado === EstadoMesa.RESERVADA) {
      mesa.estado = EstadoMesa.LIBRE;
      mesa.comensalesActuales = 0;
      this._save(STORAGE_KEY_MESAS, this.mesas);
    }
    this._save(STORAGE_KEY_RESERVAS, this.reservas);

    this._recordAudit(
      actor,
      'Reserva',
      reserva.id,
      'NO_SHOW',
      `Reserva ${reserva.codigoReserva} marcada como No-Show. Mesa #${reserva.mesaNumero} liberada.`
    );

    return this._apiResponse(reserva, `Reserva ${reserva.codigoReserva} marcada como No-Show`);
  }

  // Finalizar Reserva / Liberar Mesa
  finalizarReserva(reservaId, actor) {
    this.consumeRateLimit();
    const reserva = this.reservas.find(r => r.id === Number(reservaId));
    if (!reserva) throw new Error(`Reserva #${reservaId} no encontrada`);

    reserva.estado = EstadoReserva.FINALIZADA;
    const mesa = this.mesas.find(m => m.id === reserva.mesaId);
    if (mesa) {
      mesa.estado = EstadoMesa.EN_LIMPIEZA;
      mesa.comensalesActuales = 0;
      mesa.tiempoOcupacionMinutos = 0;
      this._save(STORAGE_KEY_MESAS, this.mesas);
    }
    this._save(STORAGE_KEY_RESERVAS, this.reservas);

    this._recordAudit(
      actor,
      'Reserva',
      reserva.id,
      'FINALIZAR_SERVICIO',
      `Servicio finalizado para ${reserva.codigoReserva}. Mesa #${reserva.mesaNumero} enviada a LIMPIEZA.`
    );

    return this._apiResponse(reserva, `Reserva ${reserva.codigoReserva} finalizada. Mesa #${reserva.mesaNumero} en limpieza.`);
  }

  // ==========================================
  // SEGURIDAD: PREVENCIÓN BOLA / IDOR (@CheckOwnership)
  // ==========================================
  cancelarReservaConValidacionOwnership(reservaId, actor) {
    this.consumeRateLimit();
    const reserva = this.reservas.find(r => r.id === Number(reservaId));
    if (!reserva) throw new Error(`Reserva #${reservaId} no existe`);

    // Inspección de Ownership simulando reflexión sobre usuario.auth0Id
    const esDuenio = reserva.usuarioAuth0Id === actor.auth0Id;
    const esAdminORecepcion = actor.rol === Roles.ROLE_ADMIN || actor.rol === Roles.ROLE_RECEPCION;

    if (!esDuenio && !esAdminORecepcion) {
      // Bloqueo estricto anti-BOLA/IDOR
      this._recordAudit(
        actor,
        'Seguridad_BOLA',
        reserva.id,
        'ACCESO_DENEGADO_IDOR',
        `ALERTA BOLA/IDOR: El usuario ${actor.auth0Id} (${actor.nombre}) intentó cancelar la reserva #${reserva.id} perteneciente a ${reserva.usuarioAuth0Id}. Petición bloqueada por @CheckOwnership.`
      );
      const error = new Error(`403 Forbidden: Violación de Autorización (BOLA / IDOR detectado). La anotación @CheckOwnership impidió que el usuario '${actor.nombre}' modifique una reserva que no le pertenece.`);
      error.isBolaError = true;
      throw error;
    }

    reserva.estado = EstadoReserva.CANCELADA;
    const mesa = this.mesas.find(m => m.id === reserva.mesaId);
    if (mesa && mesa.estado === EstadoMesa.RESERVADA) {
      mesa.estado = EstadoMesa.LIBRE;
      this._save(STORAGE_KEY_MESAS, this.mesas);
    }
    this._save(STORAGE_KEY_RESERVAS, this.reservas);

    this._recordAudit(
      actor,
      'Reserva',
      reserva.id,
      'CANCELACION_RESERVA',
      `Reserva ${reserva.codigoReserva} cancelada válidamente por ${actor.nombre}. Mesa #${reserva.mesaNumero} liberada.`
    );

    return this._apiResponse(reserva, `Reserva ${reserva.codigoReserva} cancelada exitosamente`);
  }

  getAuditLogs() {
    this.consumeRateLimit();
    return this._apiResponse(this.auditLogs, 'Registros de auditoría recuperados');
  }

  resetAllData() {
    this.salones = INITIAL_SALONES;
    this.mesas = INITIAL_MESAS;
    this.reservas = INITIAL_RESERVAS;
    this.auditLogs = INITIAL_AUDIT_LOGS;
    localStorage.removeItem(STORAGE_KEY_SALONES);
    localStorage.removeItem(STORAGE_KEY_MESAS);
    localStorage.removeItem(STORAGE_KEY_RESERVAS);
    localStorage.removeItem(STORAGE_KEY_AUDIT);
    return this._apiResponse(null, 'Base de datos de prueba restaurada al estado original');
  }
}

export const mockBackend = new MockBackendEngine();
