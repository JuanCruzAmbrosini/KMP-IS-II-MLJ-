# Fudo Resto - Plataforma de Gestión de Salón, Mesas y Turnos (Frontend React)

Aplicación web interactiva desarrollada para el **Ejercicio 5 (Parte E)** de la Guía de Trabajos Prácticos N°2 de Ingeniería del Software II (año 2026), siguiendo estrictamente la especificación arquitectónica detallada en [`PLAN.md`](../PLAN.md).

---

## 🌟 Características Principales Implementadas

### 1. Plano Interactivo del Salón (Estilo Fudo)
* **Visualización en Tiempo Real:** Representación visual de los salones (*Salón Principal*, *Terraza al Aire Libre*, *Patio Interno*, *Sector VIP & Cava*) con coordenadas $(x, y)$, formas (cuadradas, redondas, rectangulares) y distribución de sillas.
* **Código de Colores de Estado Operativo:**
  * 🟢 **Verde (`LIBRE`):** Lista para asignación.
  * 🟡 **Ámbar (`RESERVADA`):** Reserva asignada para el turno actual con nombre del cliente visible.
  * 🔴 **Rojo (`OCUPADA`):** Comensales consumiendo en mesa con cronómetro de estadía (ej. `42m`).
  * 🔵 **Azul (`EN_LIMPIEZA`):** Mesa desocupada pendiente de desinfección.
  * ⚪ **Gris (`FUERA_DE_SERVICIO`):** Bloqueada por mantenimiento.
* **Modal de Detalle y Cambio Rápido de Estado:** Acciones de un clic para rotar mesas, sentar clientes, mandar a desinfección y asignar mozo.
* **Modo Diseñador (Admin):** Soporte de arrastre dinámico (*Drag & Drop*) para reposicionar mesas en el plano visual persistiendo vía `PATCH /api/v1/mesas/{id}/posicion`.

### 2. Panel Lateral de Turno (Booking Timeline)
* Visualización cronológica de reservas según turno y fecha seleccionada.
* Filtros rápidos por estado (`CONFIRMADA`, `EN_CURSO`, `FINALIZADA`, `NO_SHOW`).
* Acciones de 1 toque:
  * **Check-in:** Sienta al comensal, pasa la reserva a `EN_CURSO` y la mesa a `OCUPADA`.
  * **Liberar Mesa:** Finaliza la reserva y envía la mesa a `EN_LIMPIEZA`.
  * **No-Show:** Libera la mesa y registra la ausencia tras el tiempo de tolerancia.
  * **Recepción Inmediata (Walk-In):** Registro ágil en puerta con auto-asignación y asiento directo.

### 3. Asignación Inteligente de Mesas (Template Method)
* Implementación del algoritmo gastronómico de optimización especificado en la Sección 4.2.B de `PLAN.md`:
  $$\min(C - \text{comensales}) \quad \text{tal que } C \ge \text{comensales}$$
* Minimiza el desperdicio de sillas evaluando la capacidad de las mesas libres del salón en tiempo real.

### 4. Flujo del Comensal (Client Booking Wizard)
* Formulario paso a paso para el cliente: Comensales (1 a 8), Fecha, Salón y Turno.
* Restricciones alimentarias (Celíaco, Vegetariano, Vegano, Alergia a mariscos, Silla de bebé).
* Confirmación instantánea con código de reserva único (ej. `RES-2026-105`) y animación de celebración.
* Panel "Mis Reservas" con gestión y cancelación de reservas propias.

### 5. Arquitectura de Seguridad Integral (Sección 5 de PLAN.md)
* **Portal de Inicio de Sesión (Universal Login Auth0):**
  * Pantalla de login inspirada en Auth0 Universal Login que permite:
    * **Acceso Rápido por Rol:** Ingreso directo a cualquiera de las 5 identidades del sistema (Recepción, Comensal, Otro Comensal para test BOLA, Mozo, Administrador).
    * **Credenciales Tradicionales:** Formulario de email y password con validación y firma simulada RS256.
  * Botón de **"Cerrar Sesión" (Salir)** en la barra de navegación para desautenticarse y regresar a la pantalla de login.
* **Autenticación Delegada Auth0 y Tokens JWT RS256:**
  * Modal interactivo "Inspector JWT Auth0" con desglose de Header, Payload (claims: `sub`, `iss`, `aud`, `roles`, `permissions`) y Firma asimétrica.
  * Inyección automática de cabecera `Authorization: Bearer <token>` en todas las llamadas API.
* **Laboratorio de Demostración Anti-BOLA / IDOR (`@CheckOwnership`):**
  * Botón interactivo *"Simular Ataque BOLA"* en "Mis Reservas": intenta cancelar la reserva de otro usuario utilizando las credenciales del comensal activo.
  * El sistema intercepta la petición, verifica la discordancia de propiedad entre el `sub` del token y `reserva.usuarioAuth0Id`, y bloquea inmediatamente con `403 Forbidden`.
* **Control de Tráfico (Rate Limiting - Token Bucket):**
  * Monitor visual del estado del cubo de tokens (límite general de 60 req/min y límite estricto de 5 reservas/min para prevenir acaparamiento de mesas).
* **Pistas de Auditoría (`@AuditAction`):**
  * Registro de auditoría que documenta cambios de estado, asignación de mesas, walk-ins e intentos de intrusión BOLA/IDOR.


### 6. Roles del Sistema (RBAC) con Selector Instantáneo
Permite alternar en tiempo real entre:
1. **Recepción (Host) - Valentina Gómez:** Mapa de mesas Fudo, check-in y walk-ins.
2. **Cliente Comensal - Mateo Peralta:** Reservas online y mis reservas.
3. **Cliente (Otro) - Lucía Fernández:** Demostración de aislamiento y protección BOLA.
4. **Mozo - Carlos Benítez:** Vista rápida de servicio y rotación ágil de mesas a limpieza/libre.
5. **Administrador - Sofía Rossi:** Diseñador de salones, configuración de turnos y métricas de seguridad.

---

## 🚀 Cómo Ejecutar la Aplicación

### Desde la raíz del Ejercicio E:
```bash
npm run dev
```

### O ingresando a la carpeta `frontend/`:
```bash
cd frontend
npm run dev
```

La aplicación se ejecutará en: **`http://localhost:5173/`**

### Para compilar el paquete de producción:
```bash
npm run build
```
