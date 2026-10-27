package com.irojas.demojwt.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.irojas.demojwt.User.Role;
import com.irojas.demojwt.User.User;
import com.irojas.demojwt.User.UserRepository;

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
                        .lastname("Demo")
                        .country("Argentina")
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
            }
            if (userRepository.findByUsername("irojas@gmail.com").isEmpty()) {
                User demoUser = User.builder()
                        .username("irojas@gmail.com")
                        .password(passwordEncoder.encode("123456"))
                        .firstname("Ivana")
                        .lastname("Rojas")
                        .country("Argentina")
                        .role(Role.USER)
                        .build();
                userRepository.save(demoUser);
            }
        };
    }
}

