# TP2 - Ejercicio 1 (c): Sistema de Gestión de Biblioteca

Este proyecto implementa el sistema solicitado en el **Ejercicio 1, inciso c)** del Trabajo Práctico Guía N°2 de Ingeniería del Software II.

## 🏛️ Arquitectura Cliente - Servidor con REST API

El sistema está dividido en dos aplicaciones independientes desarrolladas con **Java 17** y **Spring Boot**:

```text
+-------------------------------------------------------------+
|                     NAVEGADOR WEB (CLIENTE)                 |
|       Vanilla JavaScript + HTML5 Moderno + CSS3 Dark UI      |
+-------------------------------------------------------------+
                              |
                              | HTTP Fetch (JSON)
                              v
+-------------------------------------------------------------+
|             APLICACIÓN CLIENTE (Spring Boot : 8081)         |
|   - ClienteApiController (exposición REST a la vista)       |
|   - BibliotecaClienteService (utiliza RestTemplate)         |
|   - DTOs (PersonaDTO, LibroDTO, AutorDTO, etc.)             |
|   - Servidor web estático (HTML, CSS, JS)                   |
+-------------------------------------------------------------+
                              |
                              | RestTemplate (HTTP REST API)
                              v
+-------------------------------------------------------------+
|             APLICACIÓN SERVIDOR (Spring Boot : 8080)        |
|   - Controladores REST (/api/personas, /api/libros, etc.)   |
|   - Servicios transaccionales y Repositorios JPA            |
|   - Entidades del Modelo UML                                |
|   - Base de Datos persistente SQLite (biblioteca.db)        |
+-------------------------------------------------------------+
```

---

## 📐 Modelo de Clases Implementado

1. **Localidad**: `id: Long`, `denominacion: String`
2. **Domicilio**: `id: Long`, `calle: String`, `numero: int`, relación `* a 1` con `Localidad`
3. **Autor**: `id: Long`, `nombre: String`, `apellido: String`, `biografia: String`
4. **Libro**: `id: Long`, `titulo: String`, `fecha: int`, `genero: String`, `paginas: int`, `autor: String`, relación `* a *` con `Autor`
5. **Persona**: `id: Long`, `nombre: String`, `apellido: String`, `dni: int`, relación `1 a 1` con `Domicilio`, relación `1 a *` (Composición) con `Libro`

---

## 🚀 Instrucciones de Ejecución

### Requisitos

- **Java 17 o superior** (configurado en el sistema o `JAVA_HOME`).
- El proyecto incluye su propio envoltorio **Maven Wrapper (`mvnw.cmd`)**, por lo que **no** requiere tener Maven instalado previamente.

### 1. Iniciar la aplicación SERVIDOR (Puerto 8080)

Abra una terminal en la carpeta `servidor`:

```powershell
cd servidor
.\mvnw.cmd spring-boot:run
```

*El servidor creará automáticamente la base de datos `biblioteca.db` (SQLite) y cargará datos de prueba iniciales (autores, libros, localidades y domicilios).*

### 2. Iniciar la aplicación CLIENTE (Puerto 8081)

Abra otra terminal en la carpeta `cliente`:

```powershell
cd cliente
.\mvnw.cmd spring-boot:run
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
