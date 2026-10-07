# TP2 - Ejercicio 1 (f): Sistema de Gestión de Biblioteca con MapStruct

Este proyecto implementa el sistema solicitado en el **Ejercicio 1, inciso f)** del Trabajo Práctico Guía N°2 de Ingeniería del Software II.

> **Consigna:** Crear un proyecto nuevo igual al creado en el punto "C" y utilizar la librería **MapStruct** para convertir de entidad en DTO y viceversa.
> La conversión debe ser realizada en las clases de servicio, es decir las clases de servicio reciben DTOs de las clases controladoras y envían DTOs a las clases controladoras.
> **"NO PUEDEN ENVIARSE ENTIDADES A LAS CLASES CONTROLADORAS"** y **"NO PUEDEN ENVIARSE DTO'S A LAS CLASES REPOSITORIO"**.

---

## 🏛️ Arquitectura de Capas y Flujo con MapStruct

```
+-------------------------------------------------------------+
|                      CLIENTE HTTP / UI                      |
+-------------------------------------------------------------+
                              |
                       DTOs (JSON)
                              v
+-------------------------------------------------------------+
|                     CONTROLADORES REST                      |
|       (PersonaController, LibroController, etc.)            |
|       * Solo manejan DTOs, nunca tocan Entidades JPA        |
+-------------------------------------------------------------+
                              |
                       DTOs (Java Objects)
                              v
+-------------------------------------------------------------+
|                    SERVICIOS DE NEGOCIO                     |
|         (PersonaService, LibroService, etc.)                |
|                                                             |
|   [ Mapeo DTO -> Entidad ]       [ Mapeo Entidad -> DTO ]   |
|         v                             ^                     |
|     MapStruct                     MapStruct                 |
+-------------------------------------------------------------+
                              |
                     Entidades JPA (Model)
                              v
+-------------------------------------------------------------+
|                    REPOSITORIOS JPA                         |
|      (PersonaRepository, LibroRepository, etc.)             |
|      * Solo manejan Entidades JPA, nunca DTOs               |
+-------------------------------------------------------------+
                              |
                        Persistencia
                              v
+-------------------------------------------------------------+
|                 BASE DE DATOS (SQLite)                      |
+-------------------------------------------------------------+
```

---

## 📦 Componentes Implementados con MapStruct

Se definieron los DTOs y Mappers con `@Mapper(componentModel = "spring")`:

1. **DTOs en Servidor (`com.biblioteca.servidor.dto`)**:
   - `PersonaDTO`
   - `LibroDTO`
   - `AutorDTO`
   - `DomicilioDTO`
   - `LocalidadDTO`

2. **Mappers MapStruct (`com.biblioteca.servidor.mapper`)**:
   - `LocalidadMapper`: Conversión bidireccional entre `Localidad` y `LocalidadDTO`.
   - `AutorMapper`: Conversión bidireccional entre `Autor` y `AutorDTO`.
   - `DomicilioMapper`: Conversión bidireccional entre `Domicilio` y `DomicilioDTO` (utiliza `LocalidadMapper`).
   - `LibroMapper`: Conversión bidireccional entre `Libro` y `LibroDTO` (utiliza `AutorMapper`).
   - `PersonaMapper`: Conversión bidireccional entre `Persona` y `PersonaDTO` (utiliza `DomicilioMapper` y `LibroMapper`).

3. **Capa de Servicios (`com.biblioteca.servidor.service`)**:
   - Reciben DTOs desde los controladores.
   - Usan MapStruct para convertir los DTOs en entidades persistentes antes de interactuar con el repositorio.
   - Persisten y consultan únicamente entidades a través de `JpaRepository`.
   - Usan MapStruct para convertir las entidades recuperadas a DTOs antes de devolverlas al controlador.

4. **Capa de Controladores (`com.biblioteca.servidor.controller`)**:
   - Exponen y consumen exclusivamente DTOs (`PersonaDTO`, `LibroDTO`, etc.).

---

## 🚀 Instrucciones de Ejecución

### Requisitos
- **Java 17 o superior** (configurado en el sistema o `JAVA_HOME`).
- El proyecto incluye su propio envoltorio **Maven Wrapper (`mvnw.cmd`)**.

### 1. Iniciar la aplicación SERVIDOR (Puerto 8080)
Abra una terminal en la carpeta `servidor`:
```powershell
cd servidor
.\mvnw.cmd spring-boot:run
```
*(Si no tiene configurada la variable `JAVA_HOME`, puede asignarla temporalmente en consola antes de ejecutar: `set JAVA_HOME=C:\Program Files\Java\jdk-26.0.2.1`)*

### 2. Iniciar la aplicación CLIENTE (Puerto 8081)
Abra otra terminal en la carpeta `cliente`:
```powershell
cd cliente
.\mvnw.cmd spring-boot:run
```

### 3. Abrir la interfaz Web
Abra su navegador en:
```
http://localhost:8081
```

---

## 🧪 Verificación de Endpoints API (Servidor)

Todos los endpoints retornan y reciben únicamente DTOs:
- `GET /api/personas` -> Retorna `List<PersonaDTO>`
- `POST /api/personas` -> Recibe y retorna `PersonaDTO`
- `GET /api/libros` -> Retorna `List<LibroDTO>`
- `POST /api/libros` -> Recibe y retorna `LibroDTO`
- `GET /api/autores` -> Retorna `List<AutorDTO>`
- `GET /api/localidades` -> Retorna `List<LocalidadDTO>`
- `GET /api/domicilios` -> Retorna `List<DomicilioDTO>`
