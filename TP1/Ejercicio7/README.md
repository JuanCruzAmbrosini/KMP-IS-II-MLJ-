# Ejercicio 7 (e) - Testing con Inteligencia Artificial

Este directorio contiene la resolución completa de la práctica de testing para los proyectos **Gatinder** (Ejercicio 5 c) y **Videojuegos** (Ejercicio 5 d).

## 1. Estructura de Proyectos y Planes de Prueba

- [PLAN_DE_PRUEBAS_SISTEMAS.md](file:///c:/Users/Lucho/Desktop/KMP-IS-II-MLJ-/TP1/Ejercicio7/PLAN_DE_PRUEBAS_SISTEMAS.md): Documento maestro con marco teórico, ciclo STLC, matriz comparativa de tipos de prueba y explicación de los 5 patrones de automatización.
- [PLAN_DE_PRUEBAS_GATINDER.md](file:///c:/Users/Lucho/Desktop/KMP-IS-II-MLJ-/TP1/Ejercicio7/GatinderTESTS/PLAN_DE_PRUEBAS_GATINDER.md): Plan de pruebas formal para Gatinder.
- [PLAN_DE_PRUEBAS_VIDEOJUEGOS.md](file:///c:/Users/Lucho/Desktop/KMP-IS-II-MLJ-/TP1/Ejercicio7/VideojuegosTESTS/PLAN_DE_PRUEBAS_VIDEOJUEGOS.md): Plan de pruebas formal para Videojuegos.

## 2. Tipos de Pruebas Implementados en Java

1. **Pruebas Unitarias (Unit Testing)**: Con Mockito y JUnit 5 aislando la lógica de negocio de los servicios.
2. **Pruebas de Integración (Integration Testing)**: Con bases de datos en memoria (H2 y SQLite) y Spring Data JPA.
3. **Pruebas del Sistema (System Testing / E2E)**: Con MockMvc probando el flujo HTTP y renderizado de vistas.
4. **Pruebas de Aceptación (UAT / BDD)**: Escenarios de usuario estructurados en *Given-When-Then*.
5. **Pruebas Funcionales**: Validación exhaustiva de reglas de dominio y Bean Validation.
6. **Pruebas No Funcionales**: Seguridad y robustez ante excepciones y accesos anómalos.
7. **Pruebas de Regresión**: Suites que garantizan que el ciclo de vida de las entidades no se degrade.
8. **Pruebas de Rendimiento (Performance Testing)**: Validación con SLA de tiempos de respuesta (`assertTimeoutPreemptively`).
9. **Pruebas de Carga (Load Testing)**: Simulación multihilo concurrente con `ExecutorService` (30 hilos) + scripts Apache JMeter (`.jmx`).
10. **Pruebas de Estrés (Stress Testing)**: Ráfaga extrema y simultánea de peticiones masivas mediante `CountDownLatch` (100-150 peticiones) + scripts Apache JMeter (`.jmx`).

## 3. Patrones de Automatización Implementados

- **Page Object Model (POM)**: Clases `PetRegistrationPage` y `VideojuegoCrudPage` que encapsulan la UI.
- **Data-Driven Testing (DDT)**: Pruebas con `@ParameterizedTest` y `@CsvSource` para evaluar múltiples combinaciones de datos.
- **Behavior-Driven Development (BDD)**: Escenarios de negocio en lenguaje ubicuo con estructura Dado-Cuando-Entonces.
- **Pruebas de Carga y Estrés**: Implementaciones concurrentes en Java y planes `.jmx` para Apache JMeter.

## 4. Ejecución de Pruebas

Para ejecutar las pruebas en cada proyecto:

```bash
# VideojuegosTESTS
mvn test -f VideojuegosTESTS/pom.xml

# GatinderTESTS
mvn test -f GatinderTESTS/pom.xml
```

Ambas suites ejecutan **39 pruebas cada una (78 pruebas en total)** con un 100% de éxito.

