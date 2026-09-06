package com.biblioteca.config;

import com.biblioteca.model.Rol;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea un usuario ADMIN por defecto la primera vez que se levanta la aplicacion,
 * para poder ingresar y dar de alta el resto de los datos (Autores, Editoriales, Libros).
 *
 * Credenciales iniciales:
 *   mail:  admin@biblioteca.com
 *   clave: admin123
 * IMPORTANTE: cambiar esta clave despues del primer ingreso.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setDni(0L);
            admin.setNombre("Administrador");
            admin.setMail("admin@biblioteca.com");
            admin.setClave(passwordEncoder.encode("admin123"));
            admin.setRol(Rol.ADMIN);
            admin.setAlta(true);
            usuarioRepository.save(admin);
            System.out.println(">>> Usuario ADMIN creado: admin@biblioteca.com / admin123 (cambiar la clave)");
        }
    }
}
