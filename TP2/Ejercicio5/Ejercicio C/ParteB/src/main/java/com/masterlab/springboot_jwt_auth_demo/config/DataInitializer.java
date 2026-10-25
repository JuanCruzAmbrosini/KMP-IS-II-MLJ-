package com.masterlab.springboot_jwt_auth_demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.masterlab.springboot_jwt_auth_demo.user.Role;
import com.masterlab.springboot_jwt_auth_demo.user.User;
import com.masterlab.springboot_jwt_auth_demo.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initUsers() {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("password"))
                        .firstname("Admin")
                        .lastname("MasterLab")
                        .country("Argentina")
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
            }
        };
    }
}

