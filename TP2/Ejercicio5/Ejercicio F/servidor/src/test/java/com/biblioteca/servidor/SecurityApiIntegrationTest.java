package com.biblioteca.servidor;

import com.biblioteca.servidor.model.Rol;
import com.biblioteca.servidor.model.Usuario;
import com.biblioteca.servidor.repository.UsuarioRepository;
import com.biblioteca.servidor.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        if (!usuarioRepository.existsByUsername("testadmin")) {
            Usuario admin = new Usuario(null, "testadmin", passwordEncoder.encode("admin123"), "Test Admin", "admin@test.com", Rol.ROLE_ADMIN, true);
            usuarioRepository.save(admin);
        }
        if (!usuarioRepository.existsByUsername("testuser")) {
            Usuario user = new Usuario(null, "testuser", passwordEncoder.encode("user123"), "Test User", "user@test.com", Rol.ROLE_USER, true);
            usuarioRepository.save(user);
        }

        userToken = jwtTokenProvider.generateToken("testuser", Rol.ROLE_USER.name());
        adminToken = jwtTokenProvider.generateToken("testadmin", Rol.ROLE_ADMIN.name());
    }

    @Test
    @DisplayName("1. Endpoint protegido /api/personas sin token debe retornar 401 Unauthorized")
    void testGetPersonasWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/personas"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message", containsString("Token JWT valido")));
    }

    @Test
    @DisplayName("2. Endpoint protegido con token malformado o falso debe retornar 401 Unauthorized")
    void testGetPersonasWithInvalidTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/personas")
                        .header("Authorization", "Bearer token_falso_invalido_12345"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("3. Login con credenciales válidas debe retornar 200 OK y Token JWT")
    void testLoginSuccess() throws Exception {
        String loginJson = "{\"username\":\"testadmin\",\"password\":\"admin123\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.username").value("testadmin"))
                .andExpect(jsonPath("$.rol").value("ROLE_ADMIN"));
    }

    @Test
    @DisplayName("4. Login con credenciales incorrectas debe retornar 401 Unauthorized")
    void testLoginBadCredentials() throws Exception {
        String loginJson = "{\"username\":\"testadmin\",\"password\":\"password_erronea\"}";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message", containsString("Credenciales invalidas")));
    }

    @Test
    @DisplayName("5. GET /api/personas con Token de ROLE_USER debe ser permitido (200 OK)")
    void testGetPersonasWithUserTokenAllowed() throws Exception {
        mockMvc.perform(get("/api/personas")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("6. POST /api/personas con Token de ROLE_USER debe ser denegado con 403 Forbidden")
    void testPostPersonaWithUserTokenForbidden() throws Exception {
        String nuevaPersona = "{\"nombre\":\"Pedro\",\"apellido\":\"Lopez\",\"dni\":40111222}";

        mockMvc.perform(post("/api/personas")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nuevaPersona))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message", containsString("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("7. POST /api/personas con Token de ROLE_ADMIN debe ser exitoso (201 Created)")
    void testPostPersonaWithAdminTokenSuccess() throws Exception {
        String nuevaPersona = "{\"nombre\":\"Valeria\",\"apellido\":\"Sosa\",\"dni\":39888777}";

        mockMvc.perform(post("/api/personas")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(nuevaPersona))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.nombre").value("Valeria"))
                .andExpect(jsonPath("$.apellido").value("Sosa"))
                .andExpect(jsonPath("$.dni").value(39888777));
    }
}

