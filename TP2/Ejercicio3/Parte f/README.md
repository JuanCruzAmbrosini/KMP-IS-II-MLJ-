# TP2 - Ejercicio 3 (Parte f): OpenStreetMap y Georreferenciación de Proveedores

Este proyecto extiende el sistema anterior de **Carrito de Compras con Patrón Template Method** incorporando la georreferenciación de proveedores y su visualización en un mapa interactivo utilizando **OpenStreetMap (OSM)** y **Leaflet.js**.

---

## 🎯 Objetivos Implementados

1. **Investigación sobre OpenStreetMap (OSM):**
   - Fundamentos, modelo de datos topológico (Nodos, Vías, Relaciones).
   - Servidores de teselas (Tile Server) y comparativa con Google Maps API.
   - Documento completo disponible en [`INVESTIGACION_OPENSTREETMAP.md`](./INVESTIGACION_OPENSTREETMAP.md).

2. **Modelo de Proveedores con Dirección y Coordenadas:**
   - Creación de la clase [`Supplier`](src/main/java/com/ecommerce/cart/model/Supplier.java) con `address`, `latitude` y `longitude`.
   - Servicio [`SupplierService`](src/main/java/com/ecommerce/cart/service/SupplierService.java) con semillas realistas de proveedores en Mendoza y las principales provincias argentinas.
   - Controlador REST [`SupplierController`](src/main/java/com/ecommerce/cart/controller/SupplierController.java) con operaciones `GET`, `POST` y `DELETE`.

3. **Visualización y Mapa Interactivo con OpenStreetMap y Leaflet.js:**
   - Interfaz web moderna y responsiva en [`index.html`](src/main/resources/static/index.html).
   - Capa de teselas de OpenStreetMap (`https://tile.openstreetmap.org/{z}/{x}/{y}.png`).
   - Marcadores para cada proveedor con popups enriquecidos (dirección, coordenadas, teléfono, correo, categoría).
   - Interacción fluida: clic en el mapa para capturar coordenadas y dar de alta nuevos proveedores.
   - Vinculación bidireccional entre los productos del carrito de compras y sus respectivos proveedores con animación de vuelo en el mapa (`map.flyTo`).

---

## 🚀 Cómo Ejecutar el Proyecto

### Prerrequisitos
- **Java 21** o superior.
- Maven Wrapper incluido (`./mvnw`).

### 1. Ejecutar las Pruebas Automatizadas
```bash
./mvnw test
```

### 2. Iniciar el Servidor Spring Boot
```bash
./mvnw spring-boot:run
```

### 3. Abrir la Aplicación en el Navegador
Una vez iniciado el servidor, abrir:
```
http://localhost:8080/
```

---

## 📡 Endpoints de la API REST

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/suppliers` | Obtiene el listado completo de proveedores con sus coordenadas y direcciones. |
| `GET` | `/api/suppliers/{id}` | Obtiene los detalles de un proveedor específico. |
| `POST` | `/api/suppliers` | Registra un nuevo proveedor (requiere `name`, `address`, `latitude`, `longitude`). |
| `DELETE` | `/api/suppliers/{id}` | Elimina un proveedor del sistema por su ID. |
| `POST` | `/api/cart/checkout?shippingType={standard\|express}` | Ejecuta el proceso de checkout del carrito mediante el patrón **Template Method**. |

---

## 📂 Estructura del Proyecto

```
Parte f/
├── pom.xml                                   # Configuración de dependencias (Spring Boot 3.3.4, Java 21)
├── README.md                                 # Guía de inicio y resumen de la entrega
├── INVESTIGACION_OPENSTREETMAP.md            # Informe exhaustivo de investigación teórica
└── src/
    ├── main/
    │   ├── java/com/ecommerce/cart/
    │   │   ├── CartApplication.java          # Clase principal de arranque
    │   │   ├── controller/
    │   │   │   ├── CartController.java       # Controlador del Carrito
    │   │   │   └── SupplierController.java   # Controlador de Proveedores
    │   │   ├── model/
    │   │   │   ├── CartItem.java             # Ítem de compra con proveedor asociado
    │   │   │   ├── OrderSummary.java         # Resumen de orden
    │   │   │   └── Supplier.java             # Entidad de Proveedor geolocalizado
    │   │   └── service/
    │   │       ├── AbstractCartCheckoutTemplate.java  # Clase base del Template Method
    │   │       ├── StandardCartCheckout.java          # Estrategia de envío estándar
    │   │       ├── ExpressCartCheckout.java           # Estrategia de envío express
    │   │       └── SupplierService.java               # Gestión en memoria de proveedores
    │   └── resources/
    │       ├── application.properties
    │       └── static/
    │           └── index.html                # Frontend interactivo con Leaflet & OpenStreetMap
    └── test/
        └── java/com/ecommerce/cart/
            ├── CartCheckoutTest.java         # Tests unitarios de checkout
            └── SupplierServiceTest.java      # Tests unitarios de proveedores y coordenadas
```
