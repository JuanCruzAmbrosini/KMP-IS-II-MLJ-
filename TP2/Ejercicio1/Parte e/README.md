# TP2 - Ejercicio 1 (Parte e): Consumo de APIs Externas con RestTemplate y Frontend Bootstrap

## 📋 Descripción del Proyecto
Este proyecto corresponde a la resolución del **Ejercicio 1, Parte e)** del Trabajo Práctico N° 2 de **Ingeniería de Software II**. 

La aplicación consiste en una solución completa **Full Stack** construida sobre **Java 21** y **Spring Boot 3.3.2** que:
1. **Consume en el Backend APIs Externas** en tiempo real utilizando la clase `RestTemplate` de Spring Framework.
2. Expone sus propios endpoints REST desacoplados que mapean, procesan y securizan la información mediante **DTOs (Data Transfer Objects)**.
3. Sirve un **FrontEnd web moderno y responsive** maquetado con **Bootstrap 5.3**, estilos personalizados CSS, componentes visuales de dashboard y **JavaScript asíncrono (Fetch API)**.
4. Incluye un módulo interactivo para simular el consumo de la **WhatsApp Business Cloud API (Meta Graph API v19.0)** mediante `RestTemplate`.
5. Proporciona una investigación formal y detallada sobre plataformas de APIs externas (**RapidAPI, OpenWeatherMap, Magic Loops, Open-Meteo**) y el acceso y utilidad de la **API de WhatsApp** para proyectos futuros.

---

## 🏗️ Arquitectura de la Solución

```
   ┌─────────────────────────────────────────────────────────────┐
   │                  FRONTEND (Bootstrap 5.3)                   │
   │   - Dashboard climático con métricas meteorológicas        │
   │   - Pronóstico extendido a 7 días y selector multiciudad    │
   │   - Conversor dinámico de unidades (°C / °F)                │
   │   - Simulador interactivo de WhatsApp Business Cloud API    │
   └──────────────────────────────┬──────────────────────────────┘
                                  │ HTTP / Fetch JSON
                                  ▼
   ┌─────────────────────────────────────────────────────────────┐
   │               BACKEND (Spring Boot 3.3.2)                   │
   │  [ClimaController] ──> Controladores REST                    │
   │  [ClimaServiceImpl] ──> Lógica de negocio y resiliencia      │
   │  [WhatsAppMockService] ──> Construcción de payloads Meta     │
   │  [RestTemplateConfig] ──> Bean RestTemplate con Timeouts    │
   └──────────────────────────────┬──────────────────────────────┘
                                  │ RestTemplate (GET/POST)
                                  ▼
   ┌─────────────────────────────────────────────────────────────┐
   │                    APIS EXTERNAS (INTERNET)                 │
   │  1. Open-Meteo Weather Forecast & Geocoding API (Global)   │
   │  2. Meta Graph API v19.0 (WhatsApp Business Cloud API)      │
   │  3. OpenWeatherMap API (Opcional vía API Key)               │
   └─────────────────────────────────────────────────────────────┘
```

---

## ⚙️ Tecnologías Utilizadas
- **Lenguaje:** Java 21 (OpenJDK 21)
- **Framework Backend:** Spring Boot 3.3.2 (`spring-boot-starter-web`, `spring-boot-starter-test`)
- **Cliente HTTP Backend:** `org.springframework.web.client.RestTemplate`
- **Gestor de Dependencias y Construcción:** Apache Maven 3.9+
- **FrontEnd:** HTML5, CSS3 nativo, JavaScript (ES6+ asíncrono con Fetch API)
- **Framework de Estilos:** Bootstrap 5.3.3 + Bootstrap Icons 1.11.3
- **APIs Externas Integradas:**
  - *Open-Meteo Global Forecast & Geocoding API* (API meteorológica pública y de alta disponibilidad).
  - *Meta WhatsApp Cloud API v19.0* (Simulación de envío de plantillas y mensajes transaccionales).
  - *OpenWeatherMap API* (Compatible y configurable en `application.properties`).

---

## 📁 Estructura del Código Fuente

```
TP2/Ejercicio1/Parte e/
├── mvnw                               # Maven Wrapper ejecutable para Linux/macOS
├── mvnw.cmd                           # Maven Wrapper ejecutable para Windows
├── .mvn/                              # Configuración del Maven Wrapper
├── pom.xml                            # Descriptor del proyecto Maven
├── README.md                          # Guía de inicio rápido y arquitectura
├── INFORME_INVESTIGACION.md           # Informe teórico exhaustivo (APIs + WhatsApp)
└── src/
    ├── main/
    │   ├── java/com/tp2/ejercicio1/clima/
    │   │   ├── ClimaApplication.java                # Clase principal Spring Boot
    │   │   ├── config/
    │   │   │   └── RestTemplateConfig.java          # Configuración del Bean RestTemplate
    │   │   ├── controller/
    │   │   │   └── ClimaController.java             # Endpoints REST expuestos al frontend
    │   │   ├── dto/
    │   │   │   ├── ApiEstadoDTO.java                # DTO de monitoreo y latencia
    │   │   │   ├── CiudadBusquedaDTO.java           # DTO para sugerencias de autocompletado
    │   │   │   ├── ClimaActualDTO.java              # DTO con datos meteorológicos actuales
    │   │   │   ├── PronosticoCompletoDTO.java       # DTO agrupador (actual + 7 días)
    │   │   │   ├── PronosticoDiaDTO.java            # DTO de predicción por día
    │   │   │   └── WhatsAppSimulacionDTO.java       # DTO para peticiones y respuestas de Meta
    │   │   └── service/
    │   │       ├── ClimaService.java                # Interfaz del servicio de clima
    │   │       ├── ClimaServiceImpl.java            # Implementación con RestTemplate
    │   │       └── WhatsAppMockService.java         # Servicio de integración con WhatsApp
    │   └── resources/
    │       ├── application.properties               # Configuración de URLs y timeouts
    │       └── static/                              # FrontEnd servido por Spring Boot
    │           ├── index.html                       # Maqueta web con Bootstrap 5
    │           ├── css/
    │           │   └── styles.css                   # Estilos personalizados (glassmorphism/cards)
    │           └── js/
    │               └── app.js                       # Lógica de consumo de endpoints y UI
    └── test/
        └── java/com/tp2/ejercicio1/clima/
            └── ClimaApplicationTests.java           # Pruebas unitarias e integración de la API
```

---

## 🚀 Instrucciones de Ejecución

### Prerrequisitos
- **Java Development Kit (JDK):** Versión 17 o 21 instalada (`java -version`).
- **Maven:** Versión 3.8+ (o utilizar el wrapper incluido `./mvnw`).

### Pasos para Levantar la Aplicación
1. Abrir una terminal en el directorio del proyecto:
   ```bash
   cd "/home/mateoperalta/KMP-IS-II-MLJ-/TP2/Ejercicio1/Parte e"
   ```

2. Compilar y empaquetar el proyecto:
   ```bash
   mvn clean package -DskipTests
   ```
   *(O bien `./mvnw clean package -DskipTests`)*

3. Iniciar el servidor Spring Boot:
   ```bash
   mvn spring-boot:run
   ```
   *Alternativamente, ejecutando el archivo JAR generado:*
   ```bash
   java -jar target/clima-api-externa-1.0.0.jar
   ```

4. Abrir cualquier navegador web e ingresar a:
   **[http://localhost:8080/](http://localhost:8080/)**

---

## 🔌 Documentación de Endpoints REST del Backend

El backend actúa como un intermediario seguro y de alto rendimiento que oculta la complejidad y credenciales de las APIs externas:

### 1. Obtener Clima Actual
- **Método:** `GET`
- **Ruta:** `/api/clima/actual?ciudad={nombreCiudad}`
- **Ejemplo:**
  ```bash
  curl -s "http://localhost:8080/api/clima/actual?ciudad=Buenos+Aires"
  ```
- **Respuesta JSON:**
  ```json
  {
    "ciudad": "Buenos Aires",
    "pais": "Argentina",
    "region": "Ciudad Autónoma de Buenos Aires",
    "latitud": -34.61315,
    "longitud": -58.37723,
    "temperatura": 12.5,
    "sensacionTermica": 10.0,
    "humedad": 82,
    "presion": 1015.4,
    "velocidadViento": 15.5,
    "direccionViento": 176,
    "codigoClima": 3,
    "condicionDescripcion": "Nublado",
    "condicionIcono": "bi-clouds-fill text-secondary",
    "fechaHora": "08/10/2026 23:49:43",
    "fuente": "Open-Meteo Meteorological Service API v1 (via RestTemplate)"
  }
  ```

### 2. Obtener Pronóstico Extendido (7 Días)
- **Método:** `GET`
- **Ruta:** `/api/clima/pronostico?ciudad={nombreCiudad}`
- **Ejemplo:**
  ```bash
  curl -s "http://localhost:8080/api/clima/pronostico?ciudad=Mendoza"
  ```
- **Respuesta:** Objeto JSON con el clima actual más una lista de 7 objetos `PronosticoDiaDTO` con temperaturas mínimas, máximas y porcentaje de probabilidad de precipitaciones.

### 3. Autocompletado y Búsqueda de Ciudades
- **Método:** `GET`
- **Ruta:** `/api/clima/buscar?query={texto}`
- **Ejemplo:**
  ```bash
  curl -s "http://localhost:8080/api/clima/buscar?query=Cord"
  ```

### 4. Monitoreo Multiciudad en Tiempo Real
- **Método:** `GET`
- **Ruta:** `/api/clima/ciudades/populares`
- **Descripción:** Retorna el clima resumido de ciudades de referencia (Buenos Aires, Córdoba, Mendoza, Bariloche, Madrid, Miami).

### 5. Verificación de Estado de la API Externa (Health Check)
- **Método:** `GET`
- **Ruta:** `/api/clima/estado`
- **Descripción:** Mide la disponibilidad y el tiempo de respuesta (latencia en ms) hacia el servidor meteorológico externo.

### 6. Simulación de WhatsApp Business Cloud API
- **Método:** `POST`
- **Ruta:** `/api/clima/whatsapp/simular`
- **Cabecera:** `Content-Type: application/json`
- **Cuerpo:**
  ```json
  {
    "telefono": "5491144445555",
    "ciudad": "Buenos Aires",
    "temperatura": "21.5°C",
    "condicion": "Cielo despejado"
  }
  ```
- **Respuesta:** Confirmación de despacho con ID de mensaje de Meta (`wamid.HBgL...`) y trazabilidad técnica de la cabecera `Bearer`.

---

## ☕ ¿Cómo se utiliza RestTemplate en Java?

En el archivo `RestTemplateConfig.java` se define el cliente como un Bean administrado con políticas de timeout:

```java
@Configuration
public class RestTemplateConfig {
    @Value("${clima.http.connect-timeout:6000}")
    private int connectTimeout;

    @Value("${clima.http.read-timeout:6000}")
    private int readTimeout;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }
}
```

En la capa de servicio `ClimaServiceImpl.java`, se invoca la API externa mediante peticiones tipadas:

```java
// Construcción segura del URI con codificación de parámetros
URI uriPronostico = UriComponentsBuilder.fromUriString(openMeteoBaseUrl)
        .queryParam("latitude", ciudadInfo.getLatitud())
        .queryParam("longitude", ciudadInfo.getLongitud())
        .queryParam("current", "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,weather_code,surface_pressure,wind_speed_10m,wind_direction_10m")
        .queryParam("daily", "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max")
        .queryParam("timezone", "auto")
        .queryParam("forecast_days", 7)
        .build()
        .toUri();

// Consumo con RestTemplate
String jsonRespuesta = restTemplate.getForObject(uriPronostico, String.class);
```

Y para operaciones que requieren cabeceras HTTP de autorización (como en WhatsApp Cloud API con Meta):

```java
HttpHeaders headers = new HttpHeaders();
headers.setContentType(MediaType.APPLICATION_JSON);
headers.setBearerAuth(accessToken);

HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

ResponseEntity<String> response = restTemplate.exchange(
    endpointUrl,
    HttpMethod.POST,
    requestEntity,
    String.class
);
```

---

## 📑 Informe de Investigación
Para consultar el informe teórico detallado sobre **RapidAPI, OpenWeatherMap, Magic Loops** y el análisis a fondo del acceso y aplicación de la **API de WhatsApp** en proyectos futuros, consulte el documento:
➡️ **[INFORME_INVESTIGACION.md](INFORME_INVESTIGACION.md)** (o directamente desde la pestaña *"Investigación"* en la aplicación web).
