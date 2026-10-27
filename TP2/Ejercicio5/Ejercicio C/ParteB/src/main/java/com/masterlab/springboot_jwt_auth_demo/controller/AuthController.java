package com.masterlab.springboot_jwt_auth_demo.controller;

import com.masterlab.springboot_jwt_auth_demo.dto.LoginRequest;
import com.masterlab.springboot_jwt_auth_demo.dto.RegisterRequest;
import com.masterlab.springboot_jwt_auth_demo.dto.AuthResponse;
import com.masterlab.springboot_jwt_auth_demo.security.JwtService;
import com.masterlab.springboot_jwt_auth_demo.user.Role;
import com.masterlab.springboot_jwt_auth_demo.user.User;
import com.masterlab.springboot_jwt_auth_demo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Autowired(required = false)
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired(required = false)
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Simple demo user (as in the video)
        if ("admin".equals(request.getUsername()) &&
            "password".equals(request.getPassword())) {

            String token = jwtService.generateToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponse("Login successful", token));
        }

        // Database-backed users (matching Parte A)
        if (userRepository != null && passwordEncoder != null) {
            Optional<User> userOpt = userRepository.findByUsername(request.getUsername());
            if (userOpt.isPresent() && passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
                String token = jwtService.generateToken(userOpt.get().getUsername());
                return ResponseEntity.ok(new AuthResponse("Login successful", token));
            }
        }

        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        if (userRepository != null && passwordEncoder != null) {
            User user = User.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .firstname(request.getFirstname() != null ? request.getFirstname() : request.getUsername())
                    .lastname(request.getLastname() != null ? request.getLastname() : "User")
                    .country(request.getCountry() != null ? request.getCountry() : "Argentina")
                    .role(Role.USER)
                    .build();
            userRepository.save(user);
        }

        String token = jwtService.generateToken(request.getUsername());
        return ResponseEntity.ok(new AuthResponse("User registered successfully", token));
    }
}

