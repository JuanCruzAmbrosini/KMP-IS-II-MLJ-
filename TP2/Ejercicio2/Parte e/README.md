# TP2 - Ejercicio 2 (d): Archivos PDF y Excel con API REST

Este proyecto implementa las funcionalidades solicitadas en el **Ejercicio 2, inciso d)** del Trabajo Práctico Guía N°2 de Ingeniería del Software II, basado en la arquitectura del **Ejercicio 2, inciso c** (con Scheduling, Correos Automáticos y Geolocalización con Google Maps).

---

## 📋 Consigna Oficial

> **d) Archivos EXCEL y PDF:** Realizar el ejercicio de la guía audiovisual *"2) API REST PARTE 2: Archivos PDF / Archivos Excel"*. Agregar como funcionalidad al ejercicio "c" la posibilidad de descargar en archivo PDF el listado de las personas que realizaron alquiler de libros y en Excel el listado de libros disponibles.

---

## 🚀 Funcionalidades Desarrolladas

### 0. 📥 Migración desde archivo TXT
- **Archivo de entrada:** `migración.txt`, con campos separados por `;`:
  `NOMBRE;APELLIDO;DNI;CALLE;NÚMERO;`.
- La opción **Migración TXT** del menú lee el archivo mediante tokens, crea la
  persona y su domicilio, e informa registros importados, omitidos y líneas inválidas.
- Se utiliza primero el archivo `migración.txt` del directorio de ejecución del
  servidor; si no existe, se toma el archivo de ejemplo incluido en `resources`.

### 1. 📄 Descarga de Reporte en Archivo PDF: Personas con Alquiler de Libros
- **Librería utilizada:** `OpenPDF` (`com.github.librepdf:openpdf:1.3.39`), librería estándar open source compatible con `iText` recomendada para Spring Boot.
- **Contenido del reporte:**
  - Encabezado institucional con membrete, título destacado, fecha y hora exacta de emisión.
  - Tabla estilizada en orientación apaisada (**Landscape A4**) para máxima legibilidad.
  - Columnas: `# ID`, `Persona (Apellido y Nombre)`, `DNI`, `Correo Electrónico`, `Domicilio & Localidad`, `Libros Alquilados (con fecha límite de vencimiento)` y `Total de Libros`.
  - Filtro inteligente: Únicamente incluye a las personas que registran **préstamos activos** en el sistema (`libros.size() > 0`).
  - Cuadro de resumen final con total de clientes con préstamos y cantidad total de libros alquilados.
- **Acceso:** Descarga directa de archivo `reporte_personas_alquileres.pdf` o visualización en visor PDF del navegador.

### 2. 📊 Descarga de Reporte en Archivo Excel: Libros Disponibles para Préstamo
- **Librería utilizada:** `Apache POI` (`org.apache.poi:poi:5.2.5` y `poi-ooxml:5.2.5`), generando archivos modernos formato `.xlsx` (OpenXML).
- **Contenido de la planilla:**
  - Encabezado corporativo con celdas combinadas y paleta de colores azul institucional (`#1B365D`).
  - Metadatos con fecha/hora de generación y contador de ejemplares disponibles.
  - Columnas de datos: `ID Libro`, `Título del Libro`, `Autor(es)`, `Año Publicación`, `Género`, `Páginas` y `Estado`.
  - Estado estilizado con distintivo en verde ("DISPONIBLE").
  - Fila resumen al pie con el total de libros disponibles en inventario.
  - Ajuste dinámico del ancho de columnas (`autoSizeColumn`) con margen de seguridad para evitar textos cortados.
- **Filtro del catálogo:** Detecta de manera dinámica aquellos libros que **NO** están asignados actualmente a ningún lector.

### 3. 🖥️ Interfaz Web Integrada (Frontend)
- **Pestaña Dedicada "📊 Reportes PDF & Excel (2.d)":**
  - Panel KPI en tiempo real: Total de personas, personas con préstamos activos, total de libros, libros alquilados y libros disponibles.
  - Tarjetas interactivas con información del generador, descripción y botones de acción.
  - Previsualizaciones en vivo de los datos que componen cada uno de los reportes.
- **Accesos Rápidos:**
  - En la pestaña **👤 Personas**: Botón `📄 Descargar PDF Alquileres`.
  - En la pestaña **📖 Libros**: Botón `📊 Descargar Excel Disponibles`.

### 4. 🔗 Herencia Completa de Funcionalidades Anteriores
- Conserva el **Scheduling & Automatización (2.c)** con avisos por correo 24h antes del vencimiento y saludos de cumpleaños con HTML embebido.
- Conserva la **Geolocalización con Google Maps (2.b)** en domicilios de personas.

---

## 🏛️ Arquitectura y Endpoints REST

```text
+--------------------------------------------------------------------------+
|                          NAVEGADOR WEB (FRONTEND)                        |
|   - Vanilla JS + CSS3 Dark UI                                            |
|   - Pestaña "📊 Reportes PDF & Excel (2.d)" con Dashboard y Descargas    |
|   - Botones directos en pestañas de Personas y Libros                    |
+--------------------------------------------------------------------------+
                                     |
                                     | HTTP REST (GET / POST)
                                     v
+--------------------------------------------------------------------------+
|                     APLICACIÓN CLIENTE (Spring Boot : 8081)               |
|   - GET /cliente/api/reportes/alquileres/pdf       -> Descarga PDF       |
|   - GET /cliente/api/reportes/libros-disponibles/excel -> Descarga Excel |
|   - GET /cliente/api/reportes/estadisticas         -> KPIs JSON          |
|   - GET /cliente/api/reportes/alquileres/datos     -> JSON Personas      |
|   - GET /cliente/api/reportes/libros-disponibles/datos -> JSON Libros     |
+--------------------------------------------------------------------------+
                                     |
                                     | RestTemplate (HTTP)
                                     v
+--------------------------------------------------------------------------+
|                     APLICACIÓN SERVIDOR (Spring Boot : 8080)             |
|   - ReportePdfService (OpenPDF / Document / PdfPTable)                   |
|   - ReporteExcelService (Apache POI / XSSFWorkbook / XSSFSheet)          |
|   - ReporteController (/api/reportes/*)                                  |
|   - Base de Datos SQLite (biblioteca.db)                                 |
+--------------------------------------------------------------------------+
```

---

## 🛠️ Cómo Ejecutar el Proyecto

### 1. Requisitos Previos
- Java JDK 17 o superior instalado.
- Variable `JAVA_HOME` configurada (o utilizar el wrapper `./mvnw`).

### 2. Iniciar el Servidor (Backend)
```powershell
cd "TP2/Ejercicio2/Parte d/servidor"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-26.0.2.1" # o tu ruta de JDK
./mvnw spring-boot:run
```
El servidor quedará escuchando en `http://localhost:8080`.
Al arrancar, inicializa la base de datos SQLite con personas con préstamos activos (para el PDF) y libros disponibles en estantería (para el Excel).

### 3. Iniciar el Cliente (Frontend)
En una nueva terminal:
```powershell
cd "TP2/Ejercicio2/Parte d/cliente"
$env:JAVA_HOME = "C:\Program Files\Java\jdk-26.0.2.1" # o tu ruta de JDK
./mvnw spring-boot:run
```
El cliente quedará disponible en `http://localhost:8081`.

### 4. Probar las Descargas
1. Abrir `http://localhost:8081` en el navegador.
2. Ingresar a la pestaña **"📊 Reportes PDF & Excel (2.d)"**.
3. Hacer clic en **"⬇️ Descargar Reporte PDF"** para obtener `reporte_personas_alquileres.pdf`.
4. Hacer clic en **"⬇️ Descargar Reporte Excel (.xlsx)"** para obtener `libros_disponibles.xlsx`.
