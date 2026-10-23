# Especificación de Arquitectura y Plan de Desarrollo: Plataforma de Gestión de Turnos y Mesas Gastronómicas (Inspirado en Fudo)

> **Propósito de este documento (Instrucción para el Agente / Antigravity):**
> Este archivo actúa como la **Fuente Única de Verdad (Single Source of Truth)** arquitectónica del proyecto. Cada módulo, clase, endpoint y flujo que generes **DEBE** respetar estrictamente las reglas de **Alta Cohesión**, **Bajo Acoplamiento**, el patrón **Modelo-Vista-Controlador (MVC)**, el uso de **Programación Genérica (Generics)**, el patrón **Template Method**, la **Metaprogramación mediante Reflexión (Reflection)** y la arquitectura de **Seguridad (SSL/TLS, JWT, Auth0, OWASP API Security)** detallados a continuación. No omitas capas ni mezcles responsabilidades.

---

## 1. Reglas Inquebrantables de Arquitectura (Directivas para Antigravity)

Para garantizar que la lógica nunca se rompa a medida que el sistema escale, se deben cumplir las siguientes invariantes en todo el código:

1. **Prohibición de Entidades en la Capa de Presentación:** Ningún `Controller` puede recibir ni devolver una entidad de base de datos (`Entity`). Toda comunicación externa se realiza exclusivamente mediante **DTOs (Data Transfer Objects)** encapsulados en un wrapper genérico `ApiResponse<T>`.
2. **Controladores Delgados (Thin Controllers):** Los controladores solo orquestan la recepción de la petición HTTP, delegan la ejecución a la capa de `Service` y retornan el código de estado HTTP correspondiente. **Cero lógica de negocio o consultas a base de datos en el Controlador.**
3. **Inversión de Dependencias (Bajo Acoplamiento):** Las capas superiores se comunican con las inferiores a través de **Interfaces** (`IService`, `IRepository`), inyectadas por constructor.
4. **Alta Cohesión:** Cada clase tiene una única razón para cambiar (Principio de Responsabilidad Única - SRP). La lógica de mesas va en el servicio de mesas; la validación criptográfica de tokens va en los filtros de seguridad; el mapeo reflexivo va en el paquete de utilidades de reflexión.
5. **Seguridad por Diseño (Security by Design):** Todo endpoint es privado por defecto, salvo los explícitamente marcados como públicos (ej. consulta pública de horarios). Toda operación sobre un recurso propio debe validar pertenencia para mitigar vulnerabilidades **BOLA / IDOR**.

---

## 2. Dominio del Problema (Modelo Gastronómico Estilo Fudo)

El sistema modela la operación real de un restaurante dividida en **Salones**, **Mesas**, **Turnos Operativos** y **Reservas**:

### 2.1. Entidades Principales del Modelo
* **`Salon` (Room/Area):** Representa un sector físico del local (ej. *Salón Principal*, *Terraza*, *Patio Interno*, *Sector VIP*).
* **`Mesa` (Table):** Pertenece a un `Salon`. Posee número identificatorio, capacidad mínima y máxima de comensales, coordenadas $(x, y)$ para renderizado en el plano visual (estilo Fudo) y un estado operativo actual (`LIBRE`, `RESERVADA`, `OCUPADA`, `EN_LIMPIEZA`, `FUERA_DE_SERVICIO`).
* **`Turno` (Shift):** Define las franjas horarias operativas del restaurante (ej. *Almuerzo: 12:00 a 15:30*, *Cena Primer Turno: 20:00 a 22:00*, *Cena Segundo Turno: 22:15 a 00:30*), la duración estándar de ocupación por reserva (ej. $90$ o $120$ minutos) y la tolerancia de espera (ej. $15$ minutos).
* **`Reserva` (Booking):** Vincula a un `Usuario` (Cliente), una `Mesa` (o asignación automática), un `Turno`, una fecha específica, cantidad de comensales, notas especiales (alergias, silla de bebé, evento) y una máquina de estados estricta (`PENDIENTE`, `CONFIRMADA`, `EN_CURSO` / *Seated*, `FINALIZADA`, `CANCELADA`, `NO_SHOW`).
* **`Usuario` (User):** Representa a los actores del sistema sincronizados con **Auth0** (`sub` / `auth0Id`, email, nombre, teléfono y `Rol`).

### 2.2. Roles del Sistema (RBAC)
1. **`ROLE_ADMIN`:** Configura salones, planos de mesas, turnos, feriados, bloqueos de capacidad y gestiona al personal.
2. **`ROLE_RECEPCION` (Host / Encargado de Salón):** Visualiza el mapa de mesas en tiempo real (estilo Fudo), recepciona clientes (*Check-in*), asigna o cambia mesas, gestiona la lista de espera (*Walk-ins*) y marca *No-Shows*.
3. **`ROLE_MOZO` (Camarero):** Consulta el estado de las mesas de su salón asignado, las reservas próximas en sus mesas y cambia el estado de mesa de `OCUPADA` a `EN_LIMPIEZA` o `LIBRE`.
4. **`ROLE_CLIENTE` (Comensal):** Consulta disponibilidad en tiempo real, crea reservas, visualiza su historial y cancela sus propias reservas dentro del tiempo reglamentario.

---

## 3. Arquitectura MVC y Estructura de Paquetes

El proyecto debe estructurarse respetando la separación física y lógica del patrón **Modelo-Vista-Controlador (MVC)** con capa de Servicios e Infraestructura:

```text
src/
├── core/                           # Núcleo reutilizable (Generics, Template, Reflection)
│   ├── generics/
│   │   ├── BaseEntity.java         # Entidad genérica con ID y auditoría
│   │   ├── IGenericRepository.java # Contrato de persistencia genérico
│   │   ├── IGenericService.java    # Contrato de servicio genérico
│   │   ├── GenericController.java  # Controlador base parametrizado
│   │   └── ApiResponse.java        # Respuesta HTTP estandarizada ApiResponse<T>
│   ├── templates/
│   │   ├── AbstractCrudService.java          # Patrón Template Method para CRUD seguro
│   │   └── AbstractReservationProcessor.java # Patrón Template Method para flujo de reservas
│   └── reflection/
│       ├── annotations/
│       │   ├── CheckOwnership.java # Anotación para prevenir BOLA/IDOR vía Reflexión
│       │   └── AuditAction.java    # Anotación para auditoría automática
│       ├── ReflectionMapper.java   # Mapeador dinámico Entity <-> DTO y PATCH
│       └── OwnershipAspect.java    # Interceptor que usa Reflection en tiempo de ejecución
│
├── security/                       # Seguridad: SSL, JWT, Auth0, Rate Limiting, CORS
│   ├── SecurityConfig.java         # Configuración de filtros, CORS y rutas
│   ├── JwtAuth0Validator.java      # Validación criptográfica de firma y claims JWT
│   ├── RateLimitingFilter.java     # Control de tráfico anti-DoS / Fuerza bruta
│   └── GlobalExceptionHandler.java # Manejo seguro de errores (Sin StackTrace al cliente)
│
├── modules/                        # Módulos de Dominio (Alta Cohesión)
│   ├── salon/                      # MVC de Salones y Mesas (Mapa estilo Fudo)
│   │   ├── model/                  # Salon, Mesa, EstadoMesa (Entities + Repositories)
│   │   ├── dto/                    # SalonDTO, MesaEstadoDTO, PlanoSalonDTO
│   │   ├── service/                # SalonService, MesaStateService
│   │   └── controller/             # SalonController, MesaController
│   ├── turno/                      # MVC de Turnos y Disponibilidad
│   │   ├── model/                  # Turno, ConfiguracionDia
│   │   ├── dto/                    # TurnoDTO, DisponibilidadQueryDTO
│   │   ├── service/                # TurnoService, DisponibilidadEngineService
│   │   └── controller/             # TurnoController
│   └── reserva/                    # MVC de Reservas
│       ├── model/                  # Reserva, EstadoReserva
│       ├── dto/                    # CreateReservaDTO, ReservaResponseDTO, PatchReservaDTO
│       ├── service/                # ReservaService, StandardReservationProcessor, WalkInProcessor
│       └── controller/             # ReservaController
│
└── view/                           # Capa de Vista (Frontend SPA / Templates reactivos)
    ├── components/
    │   ├── FloorPlanFudo/          # Grilla interactiva de mesas por colores de estado
    │   ├── BookingTimeline/        # Línea de tiempo de turnos y ocupación
    │   └── ClientBookingWizard/    # Formulario paso a paso para el comensal
    └── services/
        └── apiClient.ts            # Interceptor Axios/Fetch que inyecta Bearer <JWT>
```

---

## 4. Especificación de Patrones de Diseño Exigidos

Antigravity debe implementar los siguientes cuatro pilares técnicos exactamente como se detallan a continuación:

### 4.1. Programación Genérica (`Generics`)
Elimina la duplicación de código en toda la aplicación manteniendo un tipado estático estricto en tiempo de compilación:

1. **`BaseEntity<ID extends Serializable>`:**
   * Atributos base heredados por todas las entidades del modelo: `ID id`, `LocalDateTime createdAt`, `LocalDateTime updatedAt`, `Boolean active` (para borrado lógico).
2. **`ApiResponse<T>`:**
   * Estructura única para toda respuesta JSON de la API:
     * `boolean success`
     * `String message`
     * `T data` (Tipado genérico: puede ser un `ReservaResponseDTO`, un `List<MesaDTO>` o `Void`)
     * `LocalDateTime timestamp`
3. **`IGenericService<T extends BaseEntity<ID>, ID extends Serializable, ReqDTO, ResDTO>`:**
   * Define las firmas universales: `ResDTO create(ReqDTO dto)`, `ResDTO findById(ID id)`, `Page<ResDTO> findAll(Pageable pageable)`, `ResDTO update(ID id, ReqDTO dto)`, `ResDTO partialUpdate(ID id, Map<String, Object> fields)`, `void softDelete(ID id)`.
4. **`GenericController<T extends BaseEntity<ID>, ID extends Serializable, ReqDTO, ResDTO>`:**
   * Expone automáticamente los endpoints base (`GET /`, `GET /{id}`, `POST /`, `PUT /{id}`, `PATCH /{id}`, `DELETE /{id}`) delegando en `IGenericService`.

---

### 4.2. Patrón `Template Method`
Se utiliza para blindar algoritmos críticos cuyo orden de pasos **jamás debe alterarse**, permitiendo que las subclases personalicen únicamente pasos específicos (*hooks*).

#### Aplicación A: `AbstractCrudService<T, ID, ReqDTO, ResDTO>`
El método principal `public final ResDTO create(ReqDTO dto)` es **`final`** (ninguna subclase puede romper su secuencia) y ejecuta:
1. `validateFormatAndBusinessRules(dto)` *(Método abstracto o hook sobrescribible)*.
2. `T entity = mapToEntity(dto)` *(Utiliza `ReflectionMapper`)*.
3. `beforeSave(entity, dto)` *(Hook para asignar estados iniciales o relaciones)*.
4. `T savedEntity = repository.save(entity)` *(Paso fijo de persistencia)*.
5. `afterSave(savedEntity)` *(Hook para auditoría o disparo de eventos)*.
6. `return mapToDto(savedEntity)`.

#### Aplicación B: `AbstractReservationProcessor` (Motor de Reservas)
En un restaurante estilo Fudo existen distintos tipos de reservas: **Reserva Online Estándar** (hecha por un cliente), **Reserva VIP / Grupo Grande** (requiere seña o aprobación manual) y **Walk-In / En Puerta** (creada por el recepcionista en el momento).
* El método plantilla `public final ReservaResponseDTO procesarReserva(CreateReservaDTO solicitud)` define el esqueleto inmutable:
  1. `validarHorarioYTurno(solicitud)` *(Paso común: verifica que el local esté abierto y el turno exista)*.
  2. `verificarReglasEspecificas(solicitud)` *(**Paso abstracto**: la subclase `StandardReservationProcessor` valida límite de hasta 6 personas y anticipación mínima; `WalkInReservationProcessor` omite la anticipación mínima)*.
  3. `Mesa mesaAsignada = asignarMejorMesaDisponible(solicitud)` *(Algoritmo de optimización que busca en el salón la mesa libre cuya capacidad $C$ minimice el desperdicio de sillas: $\min(C - \text{comensales})$ tal que $C \ge \text{comensales}$)*.
  4. `EstadoReserva estadoInicial = determinarEstadoInicial()` *(**Paso abstracto**: `CONFIRMADA` para estándar, `EN_CURSO` directo para Walk-In, `PENDIENTE` para grupos grandes)*.
  5. `Reserva reserva = persistirYActualizarPlano(solicitud, mesaAsignada, estadoInicial)` *(Paso común transaccional)*.
  6. `notificarSegunCanal(reserva)` *(Hook para enviar email/WhatsApp de confirmación)*.

---

### 4.3. Metaprogramación y Reflexión (`Reflection`)
La reflexión se aplica de forma controlada y encapsulada en el paquete `core/reflection` para resolver tres problemas complejos sin acoplar las clases:

#### Aplicación 1: Actualización Parcial Dinámica (`Dynamic PATCH`) y Mapeo
* **Clase:** `ReflectionMapper`
* **Funcionamiento:** Cuando el recepcionista mueve una mesa en el plano de Fudo o cambia solo el número de comensales de una reserva (`PATCH /api/v1/reservas/{id}`), no envía todo el objeto.
* Mediante la API de Reflexión (`Class<?>`, `Field`, `getDeclaredFields()`, `field.setAccessible(true)`), el método `applyPartialUpdate(T targetEntity, Object patchDtoOrMap)` inspecciona dinámicamente los campos en tiempo de ejecución:
  * Ignora campos nulos y campos protegidos anotados con `@NonUpdatable` (como `id`, `createdAt`, `usuarioCreadorId`).
  * Verifica compatibilidad de tipos (`isAssignableFrom`) y copia únicamente los valores modificados sobre la entidad persistida.

#### Aplicación 2: Prevención Automática de BOLA / IDOR mediante `@CheckOwnership`
* **Problema de Seguridad:** Evitar que el Cliente $A$ consulte, modifique o cancele la reserva del Cliente $B$ adivinando su ID en la URL.
* **Solución con Reflexión:**
  1. Se crea la anotación personalizada `@CheckOwnership(entityClass = Reserva.class, ownerField = "usuario.auth0Id")`.
  2. Se coloca sobre los métodos sensibles del controlador o servicio (ej. `cancelarReserva(Long id)`).
  3. Un interceptor/aspecto (`OwnershipAspect`) captura la ejecución antes de entrar al método, extrae el `ID` del recurso de los argumentos del método, recupera la entidad desde el repositorio genérico y **utiliza Reflexión para navegar el grafo de propiedades** (`usuario` $\rightarrow$ `auth0Id`).
  4. Compara ese valor con el `sub` del token JWT del usuario autenticado. Si no coinciden y el usuario no posee `ROLE_ADMIN` o `ROLE_RECEPCION`, lanza inmediatamente una `AccessDeniedException` (`403 Forbidden`) antes de que el método de negocio llegue a ejecutarse.

#### Aplicación 3: Auditoría Automática de Estados (`@AuditAction`)
* Inspecciona por reflexión el estado anterior y posterior de una `Mesa` o `Reserva` y registra automáticamente en el log de seguridad quién realizó el cambio, a qué hora y qué campos exactos mutaron.

---

## 5. Arquitectura de Seguridad Integral

El proyecto implementa los cuatro ejes de seguridad investigados:

### 5.1. Certificados SSL / TLS (HTTPS Obligatorio)
* **Entorno Local (Desarrollo):** Generación de certificado autofirmado local (vía `mkcert` o `keytool` en formato PKCS12 `.p12`) habilitando el puerto `8443` con TLS 1.3.
* **Entorno Producción:** Configuración de proxy inverso (Nginx / Cloudflare / Render / Vercel) con aprovisionamiento automático de certificados **Let's Encrypt**, forzando redirección `301 Moved Permanently` de HTTP (80) a HTTPS (443) y cabecera `Strict-Transport-Security (HSTS)`.

### 5.2. Autenticación Delegada con Auth0 y Validación JWT Stateless
1. El Frontend redirige al usuario al **Universal Login de Auth0**.
2. Una vez autenticado, Auth0 devuelve un `Access Token` en formato **JWT** firmado asimétricamente con **RS256**.
3. El Frontend adjunta el token en cada petición: `Authorization: Bearer <JWT>`.
4. El Backend **no guarda sesiones en memoria (100% Stateless)**. Utiliza el endpoint público `/.well-known/jwks.json` de Auth0 para obtener la clave pública, verificar criptográficamente la firma del JWT, validar que no haya expirado (`exp`), comprobar el `issuer` (`iss`) y el `audience` (`aud`), y extraer los roles personalizados del payload.

### 5.3. Buenas Prácticas de Seguridad en la API (Checklist de Implementación)
* **Protección BOLA / IDOR:** Implementada de forma transversal mediante la anotación `@CheckOwnership` y Reflexión.
* **Rate Limiting (Control de Tráfico):**
  * Filtro por IP / Usuario utilizando el algoritmo *Token Bucket* (ej. librería `Bucket4j` o `express-rate-limit`).
  * Límite general de API: $60$ peticiones por minuto por IP.
  * Límite estricto en `POST /api/v1/reservas`: Máximo $5$ intentos por minuto por usuario/IP para evitar que bots bloqueen todas las mesas del restaurante.
* **Validación y Sanitización de Entradas:**
  * Uso obligatorio de anotaciones de validación en todos los `ReqDTO` (`@NotNull`, `@FutureOrPresent` para fechas de reserva, `@Min(1)`, `@Max(20)` para comensales, `@Pattern` y sanitización HTML contra XSS en el campo `notasEspeciales`).
  * Consultas 100% parametrizadas vía JPA/ORM para anular cualquier vector de **Inyección SQL**.
* **CORS y Cabeceras Seguras:**
  * `Access-Control-Allow-Origin` restringido exclusivamente al dominio exacto del Frontend (prohibido usar `*` en producción).
  * Cabeceras activas: `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Content-Security-Policy`.
* **Manejo Seguro de Errores (`GlobalExceptionHandler`):**
  * Si ocurre una excepción interna, el servidor registra el *Stack Trace* completo en su sistema de logs privado, pero al cliente **solo le devuelve un `ApiResponse` limpio** con código `500` y mensaje genérico `"Error interno al procesar la solicitud"`.
* **Gestión de Secretos:**
  * Cero credenciales en el código fuente. Se provee un archivo `.env.example` con las variables requeridas: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `AUTH0_ISSUER_URI`, `AUTH0_AUDIENCE`, `CORS_ALLOWED_ORIGINS`, `SSL_KEYSTORE_PASSWORD`.

---

## 6. Plan de Ejecución Paso a Paso (Roadmap para Antigravity)

> **Instrucción para el Agente:** Ejecuta este plan fase por fase. No avances a la siguiente fase hasta que todas las clases de la fase actual compilen sin errores y respeten los contratos definidos.

### Fase 1: Configuración Base y Núcleo Genérico (`core/generics`)
1. Inicializar la estructura del proyecto y el archivo `.env.example`.
2. Crear `BaseEntity<ID>` con campos de auditoría (`createdAt`, `updatedAt`, `active`).
3. Crear la clase envolvente `ApiResponse<T>` con métodos de fábrica estáticos (`ApiResponse.ok(data, msg)`, `ApiResponse.error(msg)`).
4. Definir las interfaces `IGenericRepository<T, ID>` e `IGenericService<T, ID, ReqDTO, ResDTO>`.
5. Implementar `GenericController<T, ID, ReqDTO, ResDTO>` con los endpoints RESTful estándar.

### Fase 2: Motor de Reflexión y Esqueletos Template Method (`core/reflection` y `core/templates`)
1. Implementar `ReflectionMapper` con dos métodos clave:
   * ` <S, D> D map(S source, Class<D> destinationClass)`
   * ` <T> void copyNonNullProperties(Object sourcePatch, T targetEntity)` (respetando la anotación `@NonUpdatable`).
2. Crear las anotaciones `@NonUpdatable`, `@CheckOwnership` y `@AuditAction`.
3. Implementar `AbstractCrudService<T, ID, ReqDTO, ResDTO>` aplicando el patrón **Template Method** sobre las operaciones de creación, actualización y borrado lógico.

### Fase 3: Capa de Seguridad Perimetral (`security/`)
1. Configurar soporte SSL/HTTPS en las propiedades del servidor.
2. Implementar el filtro de validación JWT conectado a las claves públicas (JWKS) de **Auth0**, mapeando los permisos del token a los roles `ROLE_ADMIN`, `ROLE_RECEPCION`, `ROLE_MOZO` y `ROLE_CLIENTE`.
3. Implementar el aspecto/interceptor `OwnershipAspect` que procesa `@CheckOwnership` por reflexión para blindar la API contra ataques BOLA/IDOR.
4. Implementar `RateLimitingFilter` diferenciando rutas de lectura y rutas críticas de creación de reservas.
5. Implementar `GlobalExceptionHandler` para capturar excepciones de validación (`400`), autenticación (`401`), permisos/BOLA (`403`), recursos no encontrados (`404`), exceso de peticiones (`429`) y errores inesperados (`500`).

### Fase 4: Módulos de Dominio Gastronómico (`modules/salon` y `modules/turno`)
1. Modelar las entidades `Salon` y `Mesa` (con coordenadas $x, y$, capacidad y `EstadoMesa`).
2. Crear sus respectivos DTOs, Repositorios, Servicios (heredando de `AbstractCrudService`) y Controladores (heredando de `GenericController`).
3. Agregar endpoint específico en `MesaController`: `PATCH /api/v1/mesas/{id}/estado` (protegido para `ROLE_RECEPCION` y `ROLE_MOZO`) para cambiar rápidamente una mesa entre `LIBRE`, `OCUPADA` y `EN_LIMPIEZA` como en Fudo.
4. Modelar la entidad `Turno` y construir `DisponibilidadEngineService`, encargado de calcular qué mesas están libres en una fecha y turno determinados cruzando la capacidad del salón con las reservas activas.

### Fase 5: Motor de Reservas con Template Method (`modules/reserva`)
1. Modelar la entidad `Reserva` y su máquina de estados (`PENDIENTE` $\rightarrow$ `CONFIRMADA` $\rightarrow$ `EN_CURSO` $\rightarrow$ `FINALIZADA` / `CANCELADA` / `NO_SHOW`).
2. Implementar la clase abstracta `AbstractReservationProcessor` (Template Method) con el algoritmo de asignación óptima de mesa.
3. Implementar las subclases concretas:
   * `StandardReservationProcessor` (reservas web de clientes).
   * `WalkInReservationProcessor` (asignación inmediata en puerta por el recepcionista).
4. Implementar `ReservaController` aplicando `@CheckOwnership` en los endpoints de consulta individual y cancelación.

### Fase 6: Capa de Vista (Frontend / UI Estilo Fudo)
1. Configurar el SDK de **Auth0** en el cliente web para manejar el login, logout y la obtención silenciosa del JWT.
2. Construir la **Vista del Comensal**:
   * Selector de fecha, cantidad de personas y turno disponible.
   * Confirmación instantánea y panel de "Mis Reservas" con botón de cancelación.
3. Construir la **Vista Operativa de Salón (Inspirada en Fudo)**:
   * **Mapa Interactivo del Salón:** Renderizado visual de las mesas según sus coordenadas $(x, y)$ con código de colores en tiempo real (Verde = `LIBRE`, Amarillo = `RESERVADA`, Rojo = `OCUPADA`, Azul = `EN_LIMPIEZA`).
   * **Panel Lateral de Turno:** Lista cronológica de reservas del turno actual con acciones de un clic para hacer *Check-in* (pasa la reserva a `EN_CURSO` y la mesa a `OCUPADA`) o liberar mesa.

---

## 7. Criterios de Aceptación y Verificación (Checklist Final)

Antes de dar por finalizada cualquier entrega de código, verificar:
- [ ] ¿Todas las clases de Servicio y Controlador reutilizan las estructuras genéricas (`Generics`) sin repetir código CRUD?
- [ ] ¿Los métodos principales de los `Template Method` están declarados como `final` para evitar que las subclases alteren el flujo de seguridad y validación?
- [ ] ¿El mapeo de actualizaciones parciales (`PATCH`) y el control de pertenencia (`@CheckOwnership`) funcionan dinámicamente mediante `Reflection`?
- [ ] ¿Está bloqueado cualquier intento de que un usuario con `ROLE_CLIENTE` modifique o vea la reserva de otro cliente (Prueba anti-BOLA)?
- [ ] ¿El endpoint de creación de reservas devuelve `429 Too Many Requests` si se supera el umbral del Rate Limiter?
- [ ] ¿Todas las contraseñas, claves de base de datos y dominios de Auth0 se leen exclusivamente desde variables de entorno?