/**
 * Constantes y Enums del Dominio Gastronómico
 * Fuente Única de Verdad: PLAN.md
 */

export const EstadoMesa = {
  LIBRE: 'LIBRE',
  RESERVADA: 'RESERVADA',
  OCUPADA: 'OCUPADA',
  EN_LIMPIEZA: 'EN_LIMPIEZA',
  FUERA_DE_SERVICIO: 'FUERA_DE_SERVICIO'
};

export const EstadoMesaConfig = {
  LIBRE: {
    label: 'Libre',
    color: '#10b981',
    bgColor: 'rgba(16, 185, 129, 0.15)',
    borderColor: '#10b981',
    badgeClass: 'badge-libre',
    desc: 'Disponible para asignar'
  },
  RESERVADA: {
    label: 'Reservada',
    color: '#f59e0b',
    bgColor: 'rgba(245, 158, 11, 0.15)',
    borderColor: '#f59e0b',
    badgeClass: 'badge-reservada',
    desc: 'Reserva asignada para este turno'
  },
  OCUPADA: {
    label: 'Ocupada',
    color: '#ef4444',
    bgColor: 'rgba(239, 68, 68, 0.15)',
    borderColor: '#ef4444',
    badgeClass: 'badge-ocupada',
    desc: 'Comensales sentados consumiendo'
  },
  EN_LIMPIEZA: {
    label: 'En Limpieza',
    color: '#3b82f6',
    bgColor: 'rgba(59, 130, 246, 0.15)',
    borderColor: '#3b82f6',
    badgeClass: 'badge-limpieza',
    desc: 'Mesa desocupada en desinfección'
  },
  FUERA_DE_SERVICIO: {
    label: 'Fuera de Servicio',
    color: '#64748b',
    bgColor: 'rgba(100, 116, 139, 0.15)',
    borderColor: '#64748b',
    badgeClass: 'badge-inactiva',
    desc: 'Mesa bloqueada por mantenimiento'
  }
};

export const EstadoReserva = {
  PENDIENTE: 'PENDIENTE',
  CONFIRMADA: 'CONFIRMADA',
  EN_CURSO: 'EN_CURSO',
  FINALIZADA: 'FINALIZADA',
  CANCELADA: 'CANCELADA',
  NO_SHOW: 'NO_SHOW'
};

export const EstadoReservaConfig = {
  PENDIENTE: {
    label: 'Pendiente',
    color: '#f59e0b',
    badgeClass: 'badge-res-pendiente'
  },
  CONFIRMADA: {
    label: 'Confirmada',
    color: '#10b981',
    badgeClass: 'badge-res-confirmada'
  },
  EN_CURSO: {
    label: 'En Curso (Sentados)',
    color: '#8b5cf6',
    badgeClass: 'badge-res-encurso'
  },
  FINALIZADA: {
    label: 'Finalizada',
    color: '#64748b',
    badgeClass: 'badge-res-finalizada'
  },
  CANCELADA: {
    label: 'Cancelada',
    color: '#ef4444',
    badgeClass: 'badge-res-cancelada'
  },
  NO_SHOW: {
    label: 'No-Show',
    color: '#dc2626',
    badgeClass: 'badge-res-noshow'
  }
};

export const Roles = {
  ROLE_ADMIN: 'ROLE_ADMIN',
  ROLE_RECEPCION: 'ROLE_RECEPCION',
  ROLE_MOZO: 'ROLE_MOZO',
  ROLE_CLIENTE: 'ROLE_CLIENTE'
};

export const RoleDescriptions = {
  ROLE_RECEPCION: {
    name: 'Recepción (Host)',
    title: 'Encargado de Salón',
    description: 'Visualiza mapa Fudo, Check-in, Walk-ins y asignación de mesas en vivo.',
    badgeClass: 'badge-role-recepcion'
  },
  ROLE_CLIENTE: {
    name: 'Cliente Comensal',
    title: 'Comensal Registrado',
    description: 'Reserva online inteligente, consulta disponibilidad y gestiona sus reservas.',
    badgeClass: 'badge-role-cliente'
  },
  ROLE_MOZO: {
    name: 'Mozo / Camarero',
    title: 'Servicio de Mesas',
    description: 'Monitorea mesas asignadas, rota mesas ocupadas a limpieza o libres.',
    badgeClass: 'badge-role-mozo'
  },
  ROLE_ADMIN: {
    name: 'Administrador',
    title: 'Gerente Gastronómico',
    description: 'Configura planos de salones, turnos, auditoría de seguridad y analíticas.',
    badgeClass: 'badge-role-admin'
  }
};
