# Plan de Pruebas de Software - Gatinder (GatinderTESTS)

## 1. Identificación del Plan
- **Proyecto**: Gatinder - Aplicación de emparejamiento de mascotas (Adopción y Socialización)
- **Versión del Plan**: 1.0.0
- **Responsable QA**: Equipo de Testing con Asistencia de IA
- **Entorno**: Java 26 / Spring Boot 4.1.1 / SQLite in-memory / JUnit 5 / Mockito / MockMvc

## 2. Alcance de las Pruebas
### 2.1 Módulos Incluidos
- **Gestión de Mascotas (`PetService`)**: Creación, actualización, validación de campos obligatorios, baja lógica (`deleted=true`), consulta por usuario.
- **Sistema de Votos y Matches (`VoteService`)**: Emisión de votos entre mascotas, prevención de auto-votos, validación de propiedad de la mascota votante, respuesta de votos y generación de reportes consolidados (`VoteReportDto`).
- **Gestión de Usuarios y Zonas (`UserService`, `ZoneService`)**: Registro, autenticación, asignación de zona geográfica.
- **Seguridad (`SecurityConfig`)**: Restricción de endpoints protegidos (`/pet/**`, `/profile/**`, `/vote/**`) para usuarios anónimos vs. autenticados.

### 2.2 Fuera de Alcance
- Procesamiento en la nube de almacenamiento S3 para imágenes (se utilizan *fixtures* locales/mocks).
- Envío real de correos electrónicos mediante SMTP externo (se aíslan con mocks).

## 3. Estrategia de Pruebas y Casos Diseñados

### 3.1 Pruebas Unitarias (Unit Testing)
- **Objetivo**: Probar la lógica de negocio de los servicios en completo aislamiento mediante dobles de prueba (mocks).
- **Clase**: `ingsoftware.gatinder.unit.PetServiceUnitTest`
  - `testCreatePetValidData_SavesSuccessfully`: Verifica la creación con datos válidos.
  - `testCreatePetNullName_ThrowsErrorService`: Lanza excepción si el nombre es nulo o vacío.
  - `testDeletePet_SuccessWhenOwnerMatches`: Realiza baja lógica si el usuario es el dueño legítimo.
  - `testDeletePet_ThrowsWhenNotOwner`: Deniega eliminación si el usuario solicitante no es el propietario.
- **Clase**: `ingsoftware.gatinder.unit.VoteServiceUnitTest`
  - `testVoteValid_SavesVote`: Registro exitoso de voto entre dos mascotas distintas.
  - `testVoteSamePet_ThrowsErrorService`: Rechazo inmediato si se intenta votar a la misma mascota.
  - `testVoteAlienPet_ThrowsErrorService`: Deniega emitir voto con una mascota que no pertenece al usuario emisor.

### 3.2 Pruebas de Integración (Integration Testing)
- **Objetivo**: Validar la persistencia JPA real y las relaciones entre entidades (`User`, `Zone`, `Pet`, `Vote`).
- **Clase**: `ingsoftware.gatinder.integration.UserPetVoteIntegrationTest`
  - Persiste una zona, un usuario y dos mascotas en SQLite en memoria, ejecuta un voto real y verifica la consulta del reporte de votos.

### 3.3 Pruebas del Sistema (System Testing / E2E)
- **Objetivo**: Probar la integración de rutas web y el ciclo de petición/respuesta a nivel de controladores.
- **Clase**: `ingsoftware.gatinder.system.GatinderSystemFlowTest`
  - Ejecuta peticiones GET a `/`, `/login`, `/register` verificando código HTTP 200 y renderizado de plantillas Thymeleaf correspondientes.

### 3.4 Pruebas de Aceptación (UAT / BDD)
- **Objetivo**: Certificar el flujo de usuario desde el punto de vista del negocio.
- **Clase**: `ingsoftware.gatinder.acceptance.MatchAcceptanceTest`
  - **Escenario 1**: *Dado* que un usuario tiene a su mascota "Michi" registrada, *Cuando* vota por "Pelusa", *Entonces* se crea el registro de voto pendiente.
  - **Escenario 2**: *Dado* un voto recibido, *Cuando* el dueño destinatario responde afirmativamente, *Entonces* se consolida el match con fecha de respuesta.

### 3.5 Pruebas Funcionales
- **Objetivo**: Validación estricta de validaciones y reglas de negocio.
- **Clase**: `ingsoftware.gatinder.functional.PetValidationFunctionalTest`
  - Prueba límites de cadenas, géneros obligatorios y coherencia de estados de eliminación.

### 3.6 Pruebas No Funcionales (Seguridad y Resiliencia)
- **Objetivo**: Validar autenticación y autorización con Spring Security.
- **Clase**: `ingsoftware.gatinder.nonfunctional.SecurityAccessTest`
  - Acceso denegado o redirección al login cuando un usuario sin autenticar intenta acceder a rutas privadas.

### 3.7 Pruebas de Regresión
- **Clase**: `ingsoftware.gatinder.regression.GatinderRegressionSuiteTest`
  - Suite integral para validación continua ante despliegues.

### 3.8 Pruebas de Rendimiento, Carga y Estrés
- **Clases**: `ingsoftware.gatinder.performance.GatinderPerformanceTest` y `GatinderLoadAndStressTest`
  - Ejecución concurrente simulando 50 usuarios simultáneos emitiendo votos y consultando reportes.
  - Generación de plan JMeter: `src/test/resources/jmeter/gatinder_load_stress_plan.jmx`.

### 3.9 Patrones de Automatización
- **Page Object Model (POM)**: `ingsoftware.gatinder.patterns.pom.PetRegistrationPage` y `GatinderPomTest`.
- **Data-Driven Testing (DDT)**: `ingsoftware.gatinder.patterns.ddt.PetDataDrivenTest` con `@ParameterizedTest` y `@CsvSource`.

