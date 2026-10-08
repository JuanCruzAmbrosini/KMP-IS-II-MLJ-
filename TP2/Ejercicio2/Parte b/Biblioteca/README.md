# TP2 - Ejercicio 2 (b): Integración de Google Maps en Biblioteca

Este proyecto implementa las funcionalidades solicitadas en el **Ejercicio 2, inciso b)** del Trabajo Práctico Guía N°2 de Ingeniería del Software II (basado en la arquitectura del Ejercicio 1, inciso c).

## 🏛️ Arquitectura Cliente - Servidor con REST API

El sistema está dividido en dos aplicaciones independientes desarrolladas con **Java 17** y **Spring Boot**:

```text
+-------------------------------------------------------------+
|                     NAVEGADOR WEB (CLIENTE)                 |
|       Vanilla JavaScript + HTML5 Moderno + CSS3 Dark UI     |
+-------------------------------------------------------------+
                              |
                              | HTTP Fetch (JSON)
                              v
+-------------------------------------------------------------+
|             APLICACIÓN CLIENTE (Spring Boot : 8081)         |
|   - ClienteApiController (exposición REST a la vista)       |
|   - BibliotecaClienteService (utiliza RestTemplate)         |
|   - DTOs (PersonaDTO, DomicilioDTO, LibroDTO, etc.)         |
|   - Servidor web estático (HTML, CSS, JS)                   |
+-------------------------------------------------------------+
                              |
                              | RestTemplate (HTTP REST API)
                              v
+-------------------------------------------------------------+
|             APLICACIÓN SERVIDOR (Spring Boot : 8080)        |
|   - Controladores REST (/api/personas, /api/domicilios, ...) |
|   - Servicios transaccionales y Repositorios JPA            |
|   - Entidades del Modelo UML                                |
|   - Base de Datos persistente SQLite (biblioteca.db)        |
+-------------------------------------------------------------+
```

---

## 📐 Modelo de Clases Implementado

1. **Localidad**: `id: Long`, `denominacion: String`
2. **Domicilio** (Dirección): `id: Long`, `calle: String`, `numero: int`, `latitud: String`, `longitud: String`, relación `* a 1` con `Localidad`
3. **Autor**: `id: Long`, `nombre: String`, `apellido: String`, `biografia: String`
4. **Libro**: `id: Long`, `titulo: String`, `fecha: int`, `genero: String`, `paginas: int`, `autor: String`, relación `* a *` con `Autor`
5. **Persona**: `id: Long`, `nombre: String`, `apellido: String`, `dni: int`, relación `1 a 1` con `Domicilio`, relación `1 a *` (Composición) con `Libro`

---

## 🗺️ Integración con Google Maps (Ejercicio 2 - Parte b)

- **Atributos de Geolocalización**: Se incorporaron los campos `latitud` (`String`) y `longitud` (`String`) en la clase `Domicilio` (servidor) y en `DomicilioDTO` (cliente) para almacenar las coordenadas del punto de referencia de residencia de cada persona.
- **Formato del Enlace**:
  ```text
  https://www.google.com/maps?q=<latitud>,<longitud>
  ```
  Ejemplo: `https://www.google.com/maps?q=-32.88970575178735,-68.84457510855037`
- **Interfaz Web (Frontend)**:
  - En la sección de **Personas**, cada registro que posea un domicilio con coordenadas incluye un botón interactivo **📍 Google Maps** que abre en una pestaña nueva del navegador (`target="_blank"`) la ubicación en Google Maps.
  - En la sección de **Domicilios**, se agregaron campos en el formulario de creación/edición para ingresar `latitud` y `longitud`, y se visualizan en la tabla de domicilios junto a su respectivo botón de mapa.

---

## 🚀 Instrucciones de Ejecución

### Requisitos

- **Java 17 o superior** (configurado en el sistema o `JAVA_HOME`).
- El proyecto incluye su propio envoltorio **Maven Wrapper (`mvnw` / `mvnw.cmd`)**.

### 1. Iniciar la aplicación SERVIDOR (Puerto 8080)

Abra una terminal en la carpeta `servidor`:

```bash
cd servidor
./mvnw spring-boot:run
```

*El servidor cargará la base de datos `biblioteca.db` (SQLite) con datos iniciales de prueba (incluyendo coordenadas de Google Maps).*

### 2. Iniciar la aplicación CLIENTE (Puerto 8081)

Abra otra terminal en la carpeta `cliente`:

```bash
cd cliente
./mvnw spring-boot:run
```

### 3. Abrir la interfaz Web

Abra su navegador en:

```text
http://localhost:8081
```

---

## 🧪 Verificación de Endpoints API

### Servidor (`http://localhost:8080/api`)

- `GET /api/personas`
- `GET /api/libros`
- `GET /api/autores`
- `GET /api/localidades`
- `GET /api/domicilios`

### Cliente vía RestTemplate (`http://localhost:8081/cliente/api`)

- `GET /cliente/api/personas`
- `POST /cliente/api/personas`
- `PUT /cliente/api/personas/{id}`
- `DELETE /cliente/api/personas/{id}`
- *(CRUD completo idéntico para libros, autores, localidades y domicilios)*
