# TP2 - Ejercicio Nº 7: Arquitectura Microservicios / Cloud

Este directorio contiene la resolución de los puntos del Ejercicio Nº 7 del Trabajo Práctico Guía Parte Nº2 de Ingeniería del Software II (año 2026).

---

## Estructura de Contenidos

* **[EjercicioD](./EjercicioD/README.md):**  
  Implementación del ejercicio audiovisual **"7) Introducción CLOUD"** ([Guía en YouTube](https://www.youtube.com/watch?v=JJc1b5-mZDY&list=PL0W7WdoXu6O9VWgpDY10vEWt7G6xV9XTE) / [Repositorio de Referencia](https://github.com/David-DAM/spring-cloud-cero-a-experto)).
  - Ecosistema completo de microservicios con **Spring Cloud**:
    - `service-registry`: Eureka Server (Puerto 8761).
    - `config-server`: Servidor de configuración centralizada (Puerto 8088).
    - `api-gateway`: Spring Cloud Gateway con enrutamiento reactivo, rate-limiting, filtros y seguridad JWT (Puerto 8060).
    - `employee-service`: Microservicio REST de empleados con seguridad OAuth2 Resource Server (Puerto 8082).
    - `department-service`: Microservicio REST de departamentos con cliente declarativo OpenFeign y tolerancia a fallos con Resilience4j (Puerto 8083).
    - `docker-compose.yml` y `docker-config`: Keycloak SSO Server con realm preconfigurado.
    - `postman_collection.json`: Colección completa de pruebas para Postman.

