# Seguridad JWT 1 — Parte A (`demo-jwt`)

Implementación paso a paso del ejercicio de la Guía Audiovisual JWT (**Parte A**):
- **Video:** [¿Cómo crear el login? Spring Boot 3 + Spring Security 6 + JWT Authentication](https://www.youtube.com/watch?v=nwqQYCM4YT8)
- **GitHub de referencia:** [`irojascorsico/spring-boot-jwt-authentication` (v1.0)](https://github.com/irojascorsico/spring-boot-jwt-authentication/tree/v1.0)

## Estructura del Proyecto (`com.irojas.demojwt`)

- `Auth/`: `AuthController`, `AuthService`, `AuthResponse`, `LoginRequest`, `RegisterRequest`
- `Config/`: `ApplicationConfig`, `SecurityConfig`, `DataInitializer`
- `Demo/`: `DemoController` (`/api/v1/demo`)
- `Jwt/`: `JwtAuthenticationFilter`, `JwtService`
- `User/`: `User`, `Role`, `UserRepository`

## Ejecución y Pruebas

1. Levantar la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```
2. Abrir el panel visual interactivo en `http://localhost:8080` o probar por **Postman / cURL**:
   - **Registro (`POST /auth/register`):**
     ```bash
     curl -X POST http://localhost:8080/auth/register \
       -H "Content-Type: application/json" \
       -d '{"username":"irojas@gmail.com","password":"123456","firstname":"Ivana","lastname":"Rojas","country":"Argentina"}'
     ```
   - **Login (`POST /auth/login`):**
     ```bash
     curl -X POST http://localhost:8080/auth/login \
       -H "Content-Type: application/json" \
       -d '{"username":"admin","password":"password"}'
     ```
   - **Endpoint Protegido (`POST /api/v1/demo`):**
     ```bash
     curl -X POST http://localhost:8080/api/v1/demo \
       -H "Authorization: Bearer <TOKEN_JWT>"
     ```

