# Biblioteca — TP1 Ejercicio N°6 (Seguridad y Auditoría) — inciso c

Proyecto Spring Boot que implementa el modelo de datos dado (Autor, Editorial,
Imagen, Libro, Préstamo, Usuario) con arquitectura MVC + ORM (Spring Data JPA)
y seguridad con **Spring Security**.

## Requisitos

- JDK 17+
- Maven 3.9+ (o usar el wrapper si lo agregás con `mvn -N wrapper:wrapper`)
- MySQL corriendo en `localhost:3306` (o cambiar `application.properties`)

## Puesta en marcha

1. Crear/ajustar credenciales de MySQL en
   `src/main/resources/application.properties`
   (`spring.datasource.username` / `spring.datasource.password`).
   La base `biblioteca` se crea sola gracias a `createDatabaseIfNotExist=true`.

2. Compilar y ejecutar:
   ```bash
   mvn spring-boot:run
   ```

3. Abrir `http://localhost:8080`. Te va a redirigir a `/login`.

4. Al arrancar por primera vez, la app crea un usuario **ADMIN** automáticamente
   (ver `config/DataInitializer.java`):
   - mail: `admin@biblioteca.com`
   - clave: `admin123`

   Con ese usuario podés cargar Autores, Editoriales y Libros.

5. Cualquier otra persona puede registrarse desde "Crear cuenta nueva" en el
   login — queda con rol `USUARIO` y puede ver el catálogo y pedir préstamos.

## Cómo está resuelta la seguridad (para la exposición)

- **Encriptación de contraseñas**: `BCryptPasswordEncoder` (`SecurityConfig`).
  Nunca se guarda la clave en texto plano — se encripta en `UsuarioService.registrar()`.
- **Autenticación**: `CustomUserDetailsService` busca el `Usuario` por `mail`
  y arma un `UserDetails` de Spring Security con el rol como authority
  (`ROLE_ADMIN` / `ROLE_USUARIO`).
- **Autorización por URL**: configurada en `SecurityConfig.filterChain()` con
  `authorizeHttpRequests` (ej: `/autores/**` solo `ADMIN`).
- **Autorización a nivel de método**: `@PreAuthorize("hasRole('ADMIN')")` en
  algunos métodos de `LibroController` (defensa en profundidad, además de la
  regla por URL).
- **Login/logout**: `formLogin` con página propia (`login.html`) y `logout`
  configurado explícitamente.
- **Vistas condicionadas por rol**: `thymeleaf-extras-springsecurity6`
  (`sec:authorize="hasRole('ADMIN')"`) oculta botones de administración a los
  usuarios comunes.

## Estructura del proyecto

```
src/main/java/com/biblioteca/
  BibliotecaApplication.java
  model/        -> Entidades JPA (Autor, Editorial, Imagen, Libro, Prestamo, Usuario, Rol)
  repository/   -> Interfaces Spring Data JPA
  service/      -> Lógica de negocio
  controller/   -> Controladores MVC
  security/     -> CustomUserDetailsService
  config/       -> SecurityConfig, DataInitializer
src/main/resources/
  application.properties
  templates/    -> Vistas Thymeleaf (Bootstrap 5 vía CDN)
```

## Pendiente para el resto del ejercicio (no incluido acá)

- Inciso **d** y **e**: crear proyectos "Mascotas" y "Videojuegos" (de
  ejercicios anteriores) y aplicarles esta misma seguridad.
- Inciso **f**: investigación teórica sobre Auditoría (Hibernate Envers +
  Spring Data JPA auditing) — no es código, se responde aparte.
- Inciso **g**: agregar auditoría de entidades a Mascotas, Videojuegos y
  esta Biblioteca (con `@Audited` de Hibernate Envers, o los `@CreatedDate`/
  `@LastModifiedDate` de Spring Data JPA según lo que se pida).
