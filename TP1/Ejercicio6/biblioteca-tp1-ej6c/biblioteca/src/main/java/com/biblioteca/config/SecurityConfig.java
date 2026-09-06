package com.biblioteca.config;

import com.biblioteca.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion central de seguridad del sistema.
 *
 * Reglas de autorizacion:
 *  - Publico: pagina de login, registracion de usuarios, recursos estaticos (css/js).
 *  - Ver catalogo de libros (/libros/**): ADMIN y USUARIO.
 *  - Administrar Autor/Editorial/Prestamo y ABM completo de Libro: solo ADMIN.
 *  - Cualquier otra ruta: requiere estar autenticado.
 *
 * Encriptacion de contraseñas: BCrypt (hash con salt incluido, no reversible).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // habilita @PreAuthorize en controllers/servicios si se necesita
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/login", "/usuarios/registro").permitAll()
                        .requestMatchers("/libros/**").hasAnyRole("ADMIN", "USUARIO")
                        .requestMatchers("/autores/**", "/editoriales/**", "/prestamos/**", "/auditoria/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                // Con Thymeleaf + formularios server-side dejamos CSRF habilitado (default de Spring Security)
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
