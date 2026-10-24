package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.model.Rol;
import com.biblioteca.servidor.model.Usuario;
import com.biblioteca.servidor.repository.UsuarioRepository;
import com.biblioteca.servidor.security.JwtTokenProvider;
import com.biblioteca.servidor.security.dto.AuthResponse;
import com.biblioteca.servidor.security.dto.LoginRequest;
import com.biblioteca.servidor.security.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            Usuario usuario = usuarioRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String token = jwtTokenProvider.generateToken(usuario.getUsername(), usuario.getRol().name());

            AuthResponse response = new AuthResponse(
                    token,
                    "Bearer",
                    jwtTokenProvider.getJwtExpiration(),
                    usuario.getUsername(),
                    usuario.getRol().name(),
                    usuario.getNombreCompleto()
            );

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", HttpStatus.UNAUTHORIZED.value());
            error.put("error", "Unauthorized");
            error.put("message", "Credenciales invalidas: usuario o contrasena incorrectos.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        if (usuarioRepository.existsByUsername(registerRequest.getUsername())) {
            Map<String, Object> error = new HashMap<>();
            error.put("status", HttpStatus.BAD_REQUEST.value());
            error.put("error", "Bad Request");
            error.put("message", "El nombre de usuario ya se encuentra registrado.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }

        Rol rol = registerRequest.getRol() != null ? registerRequest.getRol() : Rol.ROLE_USER;

        Usuario nuevoUsuario = new Usuario(
                null,
                registerRequest.getUsername(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getNombreCompleto() != null ? registerRequest.getNombreCompleto() : registerRequest.getUsername(),
                registerRequest.getEmail(),
                rol,
                true
        );

        Usuario saved = usuarioRepository.save(nuevoUsuario);

        String token = jwtTokenProvider.generateToken(saved.getUsername(), saved.getRol().name());

        AuthResponse response = new AuthResponse(
                token,
                "Bearer",
                jwtTokenProvider.getJwtExpiration(),
                saved.getUsername(),
                saved.getRol().name(),
                saved.getNombreCompleto()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No autenticado");
        }

        Usuario usuario = usuarioRepository.findByUsername(auth.getName())
                .orElse(null);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> details = new HashMap<>();
        details.put("id", usuario.getId());
        details.put("username", usuario.getUsername());
        details.put("nombreCompleto", usuario.getNombreCompleto());
        details.put("email", usuario.getEmail());
        details.put("rol", usuario.getRol().name());
        details.put("activo", usuario.isActivo());

        return ResponseEntity.ok(details);
    }
}

