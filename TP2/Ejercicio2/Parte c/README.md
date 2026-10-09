# TP2 - Ejercicio 2 (c): Automatización de Tareas (Scheduling) y Notificaciones por Correo Electrónico

Este proyecto implementa las funcionalidades solicitadas en el **Ejercicio 2, inciso c)** del Trabajo Práctico Guía N°2 de Ingeniería del Software II (basado en la arquitectura del Ejercicio 2, inciso b con Google Maps).

## 🚀 Funcionalidades Desarrolladas

1. **Recordatorios Automáticos de Vencimiento de Libros (1 día antes / 24 horas)**:
   - Programación automática diaria mediante `@Scheduled` (expresión cron configurable en `application.properties`, por defecto `0 0 8 * * ?` a las 08:00 AM).
   - Localiza todos los préstamos activos cuya fecha de vencimiento sea exactamente al día siguiente (`LocalDate.now().plusDays(1)`).
   - Emite un correo electrónico de recordatorio a la dirección de email del usuario con los datos del libro e indicaciones de devolución.

2. **Salutación de Cumpleaños Automática**:
   - Tarea programada diaria mediante `@Scheduled` a las 08:00 AM.
   - Detecta las personas registradas que cumplen años en el día de la fecha.
   - Envía un correo con **HTML embebido festivo**, mensaje institucional de felicitación y un **botón directo que redirige a la página de la facultad** ([UTN FRM: https://frm.utn.edu.ar](https://frm.utn.edu.ar)).

3. **Arquitectura de Envío de Correo (Spring Mail + Simulador / Fallback)**:
   - Utiliza `JavaMailSender` (`spring-boot-starter-mail`).
   - Admite servidor SMTP real (Gmail u otro) configurando credenciales en `application.properties`.
   - Incluye un **simulador y logger en consola** que almacena las notificaciones en base de datos SQLite y las imprime con formato visual destacado, permitiendo evaluar y probar el sistema sin depender de credenciales externas.

4. **Panel Web de Control y Vista Previa (Frontend)**:
   - Pestaña **"📬 Automatización & Correos (2.c)"** en la interfaz web cliente.
   - Resumen del día en tiempo real: personas que cumplen años hoy y préstamos que vencen mañana.
   - Botones para **disparar manualmente** las tareas programadas para demostraciones y corrección docente.
   - Historial de notificaciones con visualizador de correo HTML en modal interactivo con iframe.

5. **Herencia del Ejercicio 2.b**:
   - Mantiene la funcionalidad de geolocalización con coordenadas (`latitud`, `longitud`) y botones directos a Google Maps para cada domicilio.

---

## 🏛️ Arquitectura Cliente - Servidor con REST API

```text
+-------------------------------------------------------------+
|                     NAVEGADOR WEB (CLIENTE)                 |
|       Vanilla JavaScript + HTML5 Moderno + CSS3 Dark UI     |
|       - Gestión CRUD de Personas, Libros, Autores, etc.      |
|       - Panel de Automatización & Historial de Notificaciones|
|       - Modal interactivo con vista previa de Email HTML     |
+-------------------------------------------------------------+
                              |
                              | HTTP Fetch (JSON)
                              v
+-------------------------------------------------------------+
|             APLICACIÓN CLIENTE (Spring Boot : 8081)         |
|   - ClienteApiController (exposición REST a la vista)       |
|   - BibliotecaClienteService (utiliza RestTemplate)         |
|   - DTOs (PersonaDTO con email/cumpleaños, LibroDTO, etc.)  |
+-------------------------------------------------------------+
                              |
                              | RestTemplate (HTTP REST API)
                              v
+-------------------------------------------------------------+
|             APLICACIÓN SERVIDOR (Spring Boot : 8080)        |
|   - BibliotecaTaskScheduler (@EnableScheduling / @Scheduled)|
|   - EmailService / EmailServiceImpl (JavaMailSender/HTML)   |
|   - NotificacionController (/api/notificaciones)           |
|   - Modelos JPA: Persona, Libro, EmailLog, Domicilio...     |
|   - Base de Datos SQLite (biblioteca.db)                    |
+-------------------------------------------------------------+
```

---

## 🛠️ Cómo Ejecutar el Proyecto

### 1. Iniciar el Servidor (Backend)
```bash
cd "TP2/Ejercicio2/Parte c/servidor"
./mvnw spring-boot:run
```
El servidor quedará disponible en `http://localhost:8080`.

### 2. Iniciar el Cliente (Frontend)
```bash
cd "TP2/Ejercicio2/Parte c/cliente"
./mvnw spring-boot:run
```
El cliente quedará disponible en `http://localhost:8081`. Abre tu navegador en esa dirección para interactuar con el sistema completo.
