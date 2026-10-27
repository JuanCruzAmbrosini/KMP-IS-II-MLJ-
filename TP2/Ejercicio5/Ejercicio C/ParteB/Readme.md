# Seguridad JWT 1 — Parte B (`springboot-jwt-auth-demo`)

Implementación paso a paso del ejercicio de la Guía Audiovisual JWT (**Parte B**):
- **Video:** [Java Spring Boot + JWT Authentication | Secure REST APIs Step-by-Step](https://www.youtube.com/watch?v=5990oFzJUa4)

## Estructura del Proyecto (`com.masterlab.springboot_jwt_auth_demo`)

- `config/`: `ApplicationConfig`, `SecurityConfig`, `DataInitializer`
- `controller/`: `AuthController` (`/api/auth/login`, `/api/auth/register`), `TestController` (`/api/hello`)
- `dto/`: `AuthResponse`, `LoginRequest`, `RegisterRequest`
- `security/`: `JwtFilter`, `JwtService`
- `user/`: `User`, `Role`, `UserRepository`

## Ejecución y Pruebas

1. Levantar la aplicación:
   ```bash
   ./mvnw spring-boot:run
   ```
2. Abrir el panel visual interactivo en `http://localhost:8080` o probar por **Postman / cURL**:
   - **Login (`POST /api/auth/login`):**
     ```bash
     curl -X POST http://localhost:8080/api/auth/login \
       -H "Content-Type: application/json" \
       -d '{"username":"admin","password":"password"}'
     ```
   - **Endpoint Protegido (`GET /api/hello`):**
     ```bash
     curl -X GET http://localhost:8080/api/hello \
       -H "Authorization: Bearer <TOKEN_JWT>"
     ```

