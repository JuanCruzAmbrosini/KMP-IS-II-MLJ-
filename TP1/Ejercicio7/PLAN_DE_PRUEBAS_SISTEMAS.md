# Documento Maestro: Plan de Pruebas de Software y Estrategia de Testing (STLC)
## Ejercicio 7 (e) - Ingeniería del Software II

---

## 1. Introducción y Marco Teórico

### 1.1 ¿Qué es un Plan de Pruebas de Software?
Un **Plan de Pruebas de Software** es un documento formal y dinámico que define de manera exhaustiva el alcance, enfoque, recursos, calendario y actividades de aseguramiento de la calidad (QA) para un proyecto determinado. Sirve como contrato técnico y hoja de ruta entre los desarrolladores, ingenieros de QA, líderes técnicos y partes interesadas (*stakeholders*), garantizando que el sistema bajo prueba cumpla tanto con las especificaciones funcionales como con los atributos de calidad no funcionales (rendimiento, seguridad, fiabilidad, mantenibilidad).

### 1.2 El Ciclo de Vida de Pruebas de Software (STLC)
El **STLC (Software Testing Life Cycle)** es un marco metodológico compuesto por fases secuenciales orientadas a certificar la calidad de cada componente y del producto integral:

```
+-----------------------------------------------------------------------+
|                      1. Análisis de Requisitos                        |
+-----------------------------------------------------------------------+
                                   |                                     
                                   v                                     
+-----------------------------------------------------------------------+
|                     2. Planificación de Pruebas                       |
+-----------------------------------------------------------------------+
                                   |                                     
                                   v                                     
+-----------------------------------------------------------------------+
|                    3. Diseño de Casos de Prueba                       |
+-----------------------------------------------------------------------+
                                   |                                     
                                   v                                     
+-----------------------------------------------------------------------+
|              4. Configuración del Entorno de Pruebas                  |
+-----------------------------------------------------------------------+
                                   |                                     
                                   v                                     
+-----------------------------------------------------------------------+
|                       5. Ejecución de Pruebas                         |
+-----------------------------------------------------------------------+
                                   |                                     
                                   v                                     
+-----------------------------------------------------------------------+
|                   6. Cierre del Ciclo de Pruebas                      |
+-----------------------------------------------------------------------+
```

1. **Análisis de Requisitos**: Estudio de historias de usuario, requerimientos funcionales y criterios de aceptación para identificar qué es testeable.
2. **Planificación de Pruebas**: Determinación de objetivos, estimación de esfuerzo, selección de herramientas y definición de la estrategia.
3. **Diseño de Casos de Prueba**: Redacción detallada de casos de prueba, preparación de datos de prueba (*test fixtures*) y scripts automatizados.
4. **Configuración del Entorno de Pruebas**: Aprovisionamiento de bases de datos aisladas (H2 en memoria, SQLite en memoria), servidores de prueba y perfiles de Spring Boot.
5. **Ejecución de Pruebas**: Ejecución de las suites (unitarias, integración, sistema, carga), registro de resultados y reporte de incidencias/bugs.
6. **Cierre del Ciclo de Pruebas**: Evaluación de métricas de cobertura, análisis de criterios de salida y emisión del informe final de calidad.

---

## 2. Matriz de Tipos de Pruebas Investigados (Punto b)

| Tipo de Prueba | Descripción | Objetivo de Ejecución | Implementación en GatinderTESTS | Implementación en VideojuegosTESTS |
| :--- | :--- | :--- | :--- | :--- |
| **Pruebas Unitarias** | Verificación aislada de componentes individuales (métodos, clases, servicios) sin dependencias externas reales. | Garantizar que cada unidad lógica funcione correctamente según su diseño interno. | `PetServiceUnitTest`, `VoteServiceUnitTest` (Mockito) | `ServicioVideojuegoUnitTest`, `ServicioCategoriaUnitTest` (Mockito) |
| **Pruebas de Integración** | Evaluación de la interacción entre capas (Servicio - Repositorio - Base de Datos). | Detectar defectos en mapeos JPA, transacciones y persistencia. | `UserPetVoteIntegrationTest` (SQLite in-memory) | `VideojuegoIntegrationTest` (H2 in-memory) |
| **Pruebas del Sistema (E2E)** | Validación de flujos completos de extremo a extremo simulando peticiones cliente-servidor. | Asegurar que la aplicación responda correctamente a través de todo el stack MVC. | `GatinderSystemFlowTest` (MockMvc sobre rutas web) | `VideojuegoSystemTest` (MockMvc sobre CRUD y vistas) |
| **Pruebas de Aceptación (UAT)** | Validación de requerimientos desde la perspectiva del usuario final (formato BDD Given-When-Then). | Confirmar que el sistema satisfaga las necesidades del negocio y del usuario. | `MatchAcceptanceTest` (Flujo de voto y match entre mascotas) | `CatalogoVideojuegosAcceptanceTest` (Búsqueda y visualización de catálogo) |
| **Pruebas Funcionales** | Comprobación de reglas de negocio específicas, límites y validaciones de datos. | Validar que el comportamiento ante entradas válidas e inválidas sea el esperado. | `PetValidationFunctionalTest` (validación de campos obligatorios) | `VideojuegoValidationFunctionalTest` (validación de precio, stock y fechas) |
| **Pruebas No Funcionales** | Verificación de atributos de calidad como seguridad, manejo de errores y robustez. | Garantizar la estabilidad operativa y protección del sistema. | `SecurityAccessTest` (control de acceso con Spring Security) | `VideojuegosNonFunctionalTest` (manejo de excepciones y errores 404/500) |
| **Pruebas de Regresión** | Reejecución automatizada de casos de prueba tras cambios o refactorizaciones. | Asegurar que nuevas modificaciones no rompan funcionalidades preexistentes. | `GatinderRegressionSuiteTest` (Suite agrupada de pruebas) | `VideojuegosRegressionSuiteTest` (Suite de regresión automatizada) |
| **Pruebas de Rendimiento** | Medición de tiempos de respuesta, latencia y uso de recursos bajo condiciones controladas. | Detectar cuellos de botella y verificar umbrales máximos tolerables (SLA). | `GatinderPerformanceTest` (`assertTimeoutPreemptively`) | `VideojuegosPerformanceTest` (`assertTimeoutPreemptively`) |
| **Pruebas de Carga** | Simulación de volumen concurrente normal o pico esperado de usuarios en producción. | Evaluar la respuesta bajo demanda esperada sostenida. | `GatinderLoadAndStressTest` (50 hilos) + Plan JMeter | `VideojuegosLoadAndStressTest` (50 hilos) + Plan JMeter |
| **Pruebas de Estrés** | Sometimiento a cargas extremas que exceden la capacidad nominal hasta el punto de rotura. | Evaluar la resiliencia y recuperación elegante (*graceful degradation*). | `GatinderLoadAndStressTest` (estrés masivo) + Script JMeter | `VideojuegosLoadAndStressTest` (estrés masivo) + Script JMeter |

---

## 3. Patrones de Automatización de Pruebas Implementados

### 3.1 Modelo de Objeto de Páginas (Page Object Model - POM)
- **Concepto**: Patrón que abstrae la interfaz de usuario en clases que representan páginas o vistas web. Encapsula los selectores HTML/Thymeleaf y las acciones del usuario (completar formularios, pulsar botones, verificar mensajes).
- **Implementación**:
  - `VideojuegoCrudPage` en `VideojuegosTESTS`: encapsula las interacciones con `/crud` y `/formulario/videojuego/{id}`.
  - `PetRegistrationPage` en `GatinderTESTS`: encapsula el formulario de registro de mascotas y perfil.

### 3.2 Pruebas Basadas en Datos (Data-Driven Testing - DDT)
- **Concepto**: Separación de los datos de entrada del flujo lógico de la prueba. Ejecuta el mismo test con múltiples combinaciones de valores válidos, inválidos y límites.
- **Implementación**:
  - `VideojuegoDataDrivenTest`: utiliza `@ParameterizedTest` y `@CsvSource` para validar reglas de negocio sobre múltiples combinaciones de precios, stock y longitudes de título.
  - `PetDataDrivenTest`: valida combinaciones de nombres, géneros y especies con conjuntos paramétricos.

### 3.3 Desarrollo Impulsado por Comportamiento (Behavior-Driven Development - BDD)
- **Concepto**: Definición de casos orientada al comportamiento esperado del sistema redactado en lenguaje de negocio con estructura **Dado (Given) - Cuando (When) - Entonces (Then)**.
- **Implementación**:
  - Escenarios BDD explícitos en `MatchAcceptanceTest` y `CatalogoVideojuegosAcceptanceTest`.

### 3.4 y 3.5 Pruebas de Carga y Estrés (Load & Stress Testing)
- **Concepto**: Simulación controlada de concurrencia para medir tiempos de respuesta (Load) y resiliencia ante saturación (Stress).
- **Implementación**:
  - **Pruebas en código Java**: Clases que ejecutan `ExecutorService`, `Callable` y `CountDownLatch` ejecutando peticiones concurrentes a los servicios con cálculo de percentiles y tasas de éxito.
  - **Planes Apache JMeter (.jmx)**: Archivos XML estándar de JMeter configurados con Thread Groups, HTTP Request Defaults, CSV Data Set Config, y Assertions de respuesta.

---

## 4. Criterios de Calidad y Definición de Terminado (DoD)

1. **Criterios de Entrada**:
   - Código fuente compila sin errores.
   - Perfil de base de datos de prueba configurado (H2/SQLite in-memory).
   - Dependencias de testing resueltas.
2. **Criterios de Salida**:
   - 100% de los casos de prueba automatizados ejecutados exitosamente (cero fallos).
   - Cobertura de todos los tipos de prueba del punto "b".
   - Tiempos de respuesta en pruebas unitarias e integración inferiores a 200 ms por caso.

