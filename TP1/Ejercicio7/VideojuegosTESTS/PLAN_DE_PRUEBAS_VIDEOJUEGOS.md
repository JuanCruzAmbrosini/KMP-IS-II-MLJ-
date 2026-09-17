# Plan de Pruebas de Software - Videojuegos (VideojuegosTESTS)

## 1. Identificación del Plan
- **Proyecto**: Catálogo y Gestión de Videojuegos (VideojuegosTESTS)
- **Versión del Plan**: 1.0.0
- **Responsable QA**: Equipo de Testing con Asistencia de IA
- **Entorno**: Java 26 / Spring Boot 2.5.4 / H2 in-memory (modo MySQL) / JUnit 5 / Mockito / MockMvc

## 2. Alcance de las Pruebas
### 2.1 Módulos Incluidos
- **Servicio de Videojuegos (`ServicioVideojuego`)**: Alta, consulta de activos (`findAllByActivo`), búsqueda por título (`findByTitle`), actualización (`updateOne`), baja lógica/toggle (`deleteById`).
- **Servicio de Categorías y Estudios (`ServicioCategoria`, `ServicioEstudio`)**: Creación y recuperación de entidades maestras.
- **Controladores Web (`controladorVideojuego`, `controlador`)**: Rutas `/`, `/crud`, `/detalle/{id}`, `/busqueda`, `/formulario/videojuego/{id}`.
- **Validaciones de Bean Validation (`@Valid`)**: Reglas de negocio sobre precio (5 a 10000), stock (1 a 10000), título no vacío, fecha no futura (`@PastOrPresent`).

### 2.2 Fuera de Alcance
- Servidor físico de MySQL (se aísla con base de datos H2 in-memory para garantizar pruebas determinísticas e independientes de la infraestructura local).

## 3. Estrategia de Pruebas y Casos Diseñados

### 3.1 Pruebas Unitarias (Unit Testing)
- **Objetivo**: Probar `ServicioVideojuego` y `ServicioCategoria` aislando el repositorio con Mockito.
- **Clase**: `com.colmena.videojuegos.unit.ServicioVideojuegoUnitTest`
  - `testFindAll_ReturnsList`: Retorna lista simulada de videojuegos.
  - `testFindById_Found`: Encuentra videojuego por ID.
  - `testSaveOne_Success`: Persiste y retorna la entidad con ID asignado.
  - `testDeleteById_TogglesActivoFlag`: Verifica que la eliminación alterna el estado `activo` (baja lógica).
- **Clase**: `com.colmena.videojuegos.unit.ServicioCategoriaUnitTest`
  - Pruebas unitarias de categorías.

### 3.2 Pruebas de Integración (Integration Testing)
- **Objetivo**: Verificar interacción de capas con base de datos H2 en memoria y consultas JPA.
- **Clase**: `com.colmena.videojuegos.integration.VideojuegoIntegrationTest`
  - Persiste un `Estudio`, una `Categoria` y un `Videojuego`, ejecuta `findAllByActivo` y verifica que la relación esté correctamente mapeada.

### 3.3 Pruebas del Sistema (System Testing / E2E)
- **Objetivo**: Probar el flujo web completo mediante MockMvc.
- **Clase**: `com.colmena.videojuegos.system.VideojuegoSystemTest`
  - GET `/` -> HTTP 200 y modelo cargado con lista de videojuegos.
  - GET `/crud` -> HTTP 200 y vista `views/crud`.
  - GET `/buscar?q=Zelda` -> HTTP 200 y vista `views/busqueda`.

### 3.4 Pruebas de Aceptación (UAT / BDD)
- **Objetivo**: Validar historias de usuario con semántica Given-When-Then.
- **Clase**: `com.colmena.videojuegos.acceptance.CatalogoVideojuegosAcceptanceTest`
  - **Escenario 1**: Consulta de catálogo activo por un cliente.
  - **Escenario 2**: Filtrado de juegos por palabra clave en la barra de búsqueda.

### 3.5 Pruebas Funcionales
- **Objetivo**: Verificar las reglas de validación de entidades (`javax.validation`).
- **Clase**: `com.colmena.videojuegos.functional.VideojuegoValidationFunctionalTest`
  - Valida restricciones de precio (<5 y >10000), stock (<1), y fecha futura.

### 3.6 Pruebas No Funcionales
- **Objetivo**: Evaluar robustez y manejo de excepciones ante peticiones anómalas.
- **Clase**: `com.colmena.videojuegos.nonfunctional.VideojuegosNonFunctionalTest`
  - Manejo de IDs inexistentes y respuesta ante entradas defectuosas.

### 3.7 Pruebas de Regresión
- **Clase**: `com.colmena.videojuegos.regression.VideojuegosRegressionSuiteTest`
  - Suite organizada para verificar que ninguna funcionalidad crítica se degrade.

### 3.8 Pruebas de Rendimiento, Carga y Estrés
- **Clases**: `com.colmena.videojuegos.performance.VideojuegosPerformanceTest` y `VideojuegosLoadAndStressTest`
  - Medición de latencia con umbrales SLA (< 100ms).
  - Simulación de concurrencia con 50 hilos concurrentes leyendo y guardando videojuegos.
  - Generación de plan JMeter: `src/test/resources/jmeter/videojuegos_load_stress_plan.jmx`.

### 3.9 Patrones de Automatización
- **Page Object Model (POM)**: `com.colmena.videojuegos.patterns.pom.VideojuegoCrudPage` y `VideojuegosPomTest`.
- **Data-Driven Testing (DDT)**: `com.colmena.videojuegos.patterns.ddt.VideojuegoDataDrivenTest` con `@ParameterizedTest` y `@CsvSource`.

