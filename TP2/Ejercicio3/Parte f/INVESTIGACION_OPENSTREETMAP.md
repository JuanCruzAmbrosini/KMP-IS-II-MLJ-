# Investigación: OpenStreetMap (OSM) y Geolocalización de Proveedores

**Asignatura:** Ingeniería del Software II  
**Trabajo Práctico N° 2:** Ejercicio 3 - Parte f  
**Tema:** OpenStreetMap, Georreferenciación con Latitud y Longitud, Integración con Spring Boot y Leaflet.js  

---

## 1. Introducción y Objetivo del Ejercicio

El presente trabajo aborda el requerimiento del **Ejercicio 3 (Parte f)** de la guía de trabajos prácticos:

> *"OpenStreetMap: Investigar y agregar al sistema anterior la dirección de los proveedores donde se guarda la latitud y longitud de cada uno de ellos. El objetivo es mostrar en un mapa la ubicación de todos los proveedores con la librería indicada."*

El sistema anterior corresponde a una plataforma de comercio electrónico desarrollada con **Java Spring Boot**, donde el proceso de liquidación y compra se implementó mediante el patrón de diseño de comportamiento **Template Method**. En esta entrega se amplía el sistema para incorporar la gestión de **Proveedores (Suppliers)** con su respectiva información geográfica y comercial, visualizándolos en un mapa interactivo alimentado por la red abierta **OpenStreetMap** a través de la librería cliente **Leaflet.js**.

---

## 2. ¿Qué es OpenStreetMap (OSM)?

**OpenStreetMap (OSM)** es una iniciativa global, colaborativa y de código abierto creada en 2004 por Steve Coast en el University College London. Su misión es construir y proveer datos geográficos del mundo de manera libre y accesible para cualquier persona, empresa u organización.

A diferencia de proveedores privativos (como Google Maps, Mapbox comercial o Apple Maps), los datos espaciales recopilados en OSM son generados mediante el esfuerzo colectivo de millones de contribuidores y están publicados bajo la licencia **Open Database License (ODbL)** de la fundación *OpenStreetMap Foundation (OSMF)*. Esto garantiza que la información puede ser leída, consultada, distribuida y personalizada sin pagar regalías por consulta ni quedar sujeto a bloqueos de cuentas por límites de cuota crediticia.

---

## 3. Arquitectura y Estructura de Datos de OpenStreetMap

OpenStreetMap no almacena mapas como imágenes estáticas en su base de datos principal, sino como un grafo topológico de elementos vectoriales estructurados en tres primitivas fundamentales:

```
       +---------------------------------------------+
       |                  RELACIÓN                   |
       |  (Rutas, límites comunales, multipolígonos) |
       +---------------------------------------------+
                              |
                              v
       +---------------------------------------------+
       |                     VÍA                     |
       |    (Calles, avenidas, ríos, polígonos)      |
       +---------------------------------------------+
                              |
                              v
       +---------------------------------------------+
       |                    NODO                     |
       |  (Punto lat/lon: Proveedor, POI, vértice)   |
       +---------------------------------------------+
```

1. **Nodos (Nodes):**
   - Son puntos individuales en el espacio geográfico representados por un par de coordenadas angulares: **Latitud** ($\phi \in [-90, +90]$) y **Longitud** ($\lambda \in [-180, +180]$) según el estándar geodésico **WGS 84 (EPSG:4326)**.
   - En nuestro sistema, cada **Proveedor** se mapea conceptualmente como un nodo con atributos de contacto y categorización.

2. **Vías (Ways):**
   - Secuencias ordenadas de entre 2 y 2.000 nodos.
   - Si la secuencia es abierta, representa elementos lineales como calles, avenidas, vías de tren o ríos.
   - Si la secuencia es cerrada (el primer y último nodo coinciden), representa un área o polígono (por ejemplo, el perímetro de un edificio, un parque o un centro de distribución).

3. **Relaciones (Relations):**
   - Agrupaciones lógicas de nodos, vías u otras relaciones subordinadas que expresan conectividad de alto nivel (como recorridos de líneas de colectivos, límites departamentales o polígonos con agujeros interiores).

4. **Etiquetas (Tags / Clave-Valor):**
   - Cada primitiva espacial almacena pares `clave=valor` (ejemplo: `shop=computer`, `name=TechSupply`, `addr:city=Mendoza`).

---

## 4. Servidores de Teselas (Slippy Map Tiles)

Para mostrar mapas continuos en navegadores y aplicaciones móviles de forma rápida y sin transferir terabytes de información vectorial pura en cada desplazamiento, OpenStreetMap utiliza el esquema de **Teselas Deslizantes (Slippy Tiles)**:

- El globo terráqueo se proyecta al plano mediante la proyección **Web Mercator (EPSG:3857)**.
- El mapa se segmenta en niveles de zoom que van desde **$z=0$** (donde todo el planeta entra en una sola imagen de $256 \times 256$ píxeles) hasta **$z=19$** (nivel de detalle a escala de edificios y veredas).
- Cada imagen es servida mediante un esquema estándar de URL RESTful:
  $$\text{https://\{s\}.tile.openstreetmap.org/\{z\}/\{x\}/\{y\}.png}$$
  donde:
  - `{s}` es el subdominio de balanceo de carga (`a`, `b`, o `c`).
  - `{z}` es el nivel de zoom actual.
  - `{x}` e `{y}` son las coordenadas de la tesela en la cuadrícula plana.

---

## 5. Librerías de Visualización: ¿Por qué Leaflet.js?

Tal como se documenta en las referencias bibliográficas propuestas en la guía audiovisual del TP (*Herman Maleiane: Spring Boot + Leaflet.js Mapping* y *Abhishek Ranjan Dev: Geolocation with OpenStreetMap & Spring Boot*):

**Leaflet.js** es la librería de JavaScript de código abierto líder para mapas interactivos web:
- **Ligereza:** Su núcleo pesa menos de 40 KB minimizado y gzippeado, lo que la hace sumamente superior en tiempos de carga frente a alternativas pesadas.
- **Independencia de proveedores:** Puede alimentarse con teselas estándar de OpenStreetMap, capas humanitarias (HOT), satelitales o servidores propios.
- **Soporte táctil y responsivo:** Excelente rendimiento en dispositivos móviles y de escritorio.
- **API amigable:** Maneja capas (`L.tileLayer`), marcadores (`L.marker`), popups (`L.popup`), grupos de capas (`L.layerGroup`) y eventos de usuario (`click`, `mousemove`, `zoom`).

Otras alternativas existentes en el ecosistema son:
- **OpenLayers:** Más potente para análisis GIS avanzado y proyecciones complejas, pero con una curva de aprendizaje y peso de descarga significativamente mayores.
- **MapLibre GL JS / Mapbox GL:** Basadas en WebGL para renderizado de teselas vectoriales con rotación 3D, pero con mayor demanda de GPU en el cliente.

---

## 6. Cuadro Comparativo: OpenStreetMap vs Google Maps

| Característica / Criterio | OpenStreetMap (OSM) + Leaflet.js | Google Maps Platform |
| :--- | :--- | :--- |
| **Costo y Facturación** | **100% Gratuito y Libre**. No requiere ingresar tarjeta de crédito ni arriesgarse a facturas imprevistas. | **Pago por uso**. Requiere cuenta de facturación en Google Cloud y cobra por cada 1.000 cargas de mapa tras consumir el crédito mensual. |
| **Licencia de Uso** | Open Database License (ODbL) de código abierto. | Propietaria y restrictiva. Prohíbe almacenar en caché coordenadas o combinar datos con otras fuentes sin autorización expresa. |
| **Privacidad de Usuarios** | No recolecta datos de rastreo publicitario ni telemetría invasiva. | Monitorea actividad, perfiles y ubicación de los usuarios finales según los términos de Google. |
| **Posibilidad de Self-Hosting** | **Total**. Se puede montar un servidor propio de teselas con Docker, PostgreSQL/PostGIS y TileServer-GL. | **Imposible**. Exclusivamente alojado y dependiente de la infraestructura de Google. |
| **Personalización de Capas** | Permite intercalar capas gratuitas (Humanitario, Topográfico, Ciclovías, Satelital). | Estilos visuales limitados a la plataforma de Google Maps. |

---

## 7. Geocodificación y Servicios Adicionales

1. **Geocodificación Directa (Forward Geocoding):** Proceso de traducir una dirección textual en formato postal (ej. *"Av. San Martín 1020, Mendoza"*) a coordenadas angulares (`lat: -32.89084, lon: -68.84584`). El motor oficial libre de OSM para esta tarea es **Nominatim**.
2. **Geocodificación Inversa (Reverse Geocoding):** Operación contraria; a partir de un clic en el mapa (`lat`, `lng`), se consulta al motor para recuperar el nombre de la calle, barrio y ciudad.
3. **Overpass API:** Motor de consultas de sólo lectura optimizado para extraer subconjuntos de datos de OSM según criterios semánticos (por ejemplo, buscar todas las farmacias o distribuidores en un radio de 5 km).

---

## 8. Arquitectura e Implementación en el Proyecto

El sistema anterior de **Carrito de Compras con Template Method** ha sido expandido de forma modular e integral en la subcarpeta `Parte f`:

```
Parte f/
├── pom.xml                                   # Configuración de Maven (Spring Boot 3.3.4, Java 21)
├── README.md                                 # Resumen ejecutivo y guía de inicio rápido
├── INVESTIGACION_OPENSTREETMAP.md            # Este informe de investigación técnica
└── src/
    ├── main/
    │   ├── java/com/ecommerce/cart/
    │   │   ├── CartApplication.java          # Punto de entrada de la aplicación Spring Boot
    │   │   ├── controller/
    │   │   │   ├── CartController.java       # Endpoint de checkout con Template Method
    │   │   │   └── SupplierController.java   # API RESTful de Proveedores con coordenadas y direcciones
    │   │   ├── model/
    │   │   │   ├── CartItem.java             # Ítem de carrito con referencia a proveedor
    │   │   │   ├── OrderSummary.java         # DTO de resumen de orden
    │   │   │   └── Supplier.java             # Entidad Proveedor (id, name, address, lat, lon, city...)
    │   │   └── service/
    │   │       ├── AbstractCartCheckoutTemplate.java  # Clase abstracta con método plantilla invariable
    │   │       ├── StandardCartCheckout.java          # Implementación concreta: envío estándar
    │   │       ├── ExpressCartCheckout.java           # Implementación concreta: envío express
    │   │       └── SupplierService.java               # Servicio de gestión y semillas de proveedores
    │   └── resources/
    │       ├── application.properties
    │       └── static/
    │           └── index.html                 # UI moderna e integrada: Carrito + Mapa OSM + Informe
    └── test/
        └── java/com/ecommerce/cart/
            ├── CartCheckoutTest.java         # Pruebas unitarias del Template Method
            └── SupplierServiceTest.java      # Pruebas unitarias de georreferenciación de proveedores
```

### 8.1. Modelo del Proveedor (`Supplier.java`)
Contiene los atributos exigidos por la consigna:
- `address`: Dirección física descriptiva.
- `latitude`: Coordenada de latitud en formato numérico flotante de doble precisión (`Double`).
- `longitude`: Coordenada de longitud en formato numérico flotante de doble precisión (`Double`).
- Datos complementarios: `id`, `name`, `city`, `category`, `phone`, `contactEmail`.

### 8.2. Endpoints RESTful (`SupplierController.java`)
- `GET /api/suppliers`: Retorna la colección completa de proveedores con sus coordenadas y direcciones.
- `GET /api/suppliers/{id}`: Retorna el detalle de un proveedor por su identificador único.
- `POST /api/suppliers`: Permite dar de alta dinámicamente nuevos proveedores ingresando su dirección y coordenadas geográficas.
- `DELETE /api/suppliers/{id}`: Elimina un proveedor del sistema.

### 8.3. Interfaz de Usuario e Integración con OpenStreetMap (`index.html`)
- **Renderizado Dinámico:** Leaflet.js dibuja el mapa con las teselas oficiales de OpenStreetMap.
- **Popups Enriquecidos:** Al hacer clic en un marcador, se despliega una tarjeta con el nombre del proveedor, dirección exacta, ciudad, teléfono, correo y coordenadas formateadas.
- **Interacción Bidireccional:**
  1. Al presionar el botón *"Ver en Mapa"* desde un producto del Carrito de Compras, el sistema cambia automáticamente de pestaña y ejecuta una animación suave de vuelo (`map.flyTo`) hacia el proveedor que suministra dicho artículo.
  2. Al hacer clic directamente en cualquier punto geográfico del mapa, se capturan en tiempo real las coordenadas `lat` y `lng` del cursor y se ofrece registrarlas en un nuevo proveedor.
- **Filtro Reactivo:** Cuadro de búsqueda instantánea por nombre de empresa, ciudad o rubro tecnológico.
- **Ajuste de Vista:** Botones para enfocar toda la red nacional (`fitBounds`), la región de Cuyo/Mendoza o el país completo.

---

## 9. Verificación y Resultados de Pruebas

Se ejecutaron las pruebas automatizadas con el comando:
```bash
./mvnw test
```
**Resultado:**
- `CartCheckoutTest`: 3 pruebas superadas (verificación de algoritmos de checkout estándar y express mediante Template Method).
- `SupplierServiceTest`: 3 pruebas superadas (validación de carga de datos iniciales, rango de coordenadas espaciales $[-90, 90]$ y $[-180, 180]$, altas y bajas en tiempo de ejecución).
- **Total:** 6 pruebas exitosas sin errores ni fallos.

---

## 10. Conclusiones

La incorporación de **OpenStreetMap** mediante **Leaflet.js** en una arquitectura basada en **Spring Boot** demuestra ser una solución altamente eficiente, moderna y sostenible:
1. **Autonomía Operativa:** Elimina los costos recurrentes y restricciones de licencias de APIs propietarias como Google Maps.
2. **Coherencia Arquitectónica:** La separación en capas (Controlador REST $\rightarrow$ Servicio $\rightarrow$ Modelo $\rightarrow$ Frontend SPA con Leaflet) mantiene un acoplamiento bajo y alta cohesión.
3. **Experiencia de Usuario Integral:** Vincula orgánicamente el proceso transaccional del carrito de compras (patrón Template Method) con la cadena de valor física y logística de los proveedores en el territorio.
