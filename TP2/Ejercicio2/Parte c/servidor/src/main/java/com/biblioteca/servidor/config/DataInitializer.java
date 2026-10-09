package com.biblioteca.servidor.config;

import com.biblioteca.servidor.model.*;
import com.biblioteca.servidor.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(
            LocalidadRepository localidadRepository,
            DomicilioRepository domicilioRepository,
            AutorRepository autorRepository,
            LibroRepository libroRepository,
            PersonaRepository personaRepository) {
        return args -> {
            if (localidadRepository.count() == 0) {
                // 1. Localidades
                Localidad l1 = localidadRepository.save(new Localidad(null, "Godoy Cruz"));
                Localidad l2 = localidadRepository.save(new Localidad(null, "Ciudad de Mendoza"));
                Localidad l3 = localidadRepository.save(new Localidad(null, "Guaymallén"));

                // 2. Domicilios con coordenadas de Google Maps (del Ejercicio 2.b)
                Domicilio d1 = domicilioRepository.save(new Domicilio(null, "San Martín", 1040, "-32.88970575178735", "-68.84457510855037", l1));
                Domicilio d2 = domicilioRepository.save(new Domicilio(null, "Belgrano", 520, "-32.890840", "-68.845870", l2));
                Domicilio d3 = domicilioRepository.save(new Domicilio(null, "Colón", 310, "-32.895520", "-68.835120", l3));

                // 3. Autores
                Autor a1 = autorRepository.save(new Autor(null, "Gabriel", "García Márquez", "Premio Nobel de Literatura 1982"));
                Autor a2 = autorRepository.save(new Autor(null, "Jorge Luis", "Borges", "Escritor y poeta argentino de renombre universal"));
                Autor a3 = autorRepository.save(new Autor(null, "Julio", "Cortázar", "Autor de Rayuela e innovador de la narrativa"));

                // 4. Libros con fechas de vencimiento de devolución
                // lib1: Vence EXACTAMENTE MAÑANA (1 día antes del vencimiento para activar el recordatorio automático)
                Libro lib1 = libroRepository.save(new Libro(
                        null,
                        "Cien Años de Soledad",
                        1967,
                        "Realismo Mágico",
                        417,
                        "Gabriel García Márquez",
                        LocalDate.now().plusDays(1),
                        new ArrayList<>(List.of(a1))
                ));

                // lib2: Vence en 7 días
                Libro lib2 = libroRepository.save(new Libro(
                        null,
                        "Ficciones",
                        1944,
                        "Cuentos / Fantasía",
                        224,
                        "Jorge Luis Borges",
                        LocalDate.now().plusDays(7),
                        new ArrayList<>(List.of(a2))
                ));

                // lib3: Vence en 14 días
                Libro lib3 = libroRepository.save(new Libro(
                        null,
                        "Rayuela",
                        1963,
                        "Novela Experimental",
                        600,
                        "Julio Cortázar",
                        LocalDate.now().plusDays(14),
                        new ArrayList<>(List.of(a3))
                ));

                // 5. Personas con emails y fechas de nacimiento
                // p1: Cumpleaños es HOY (coincide día y mes actual) y tiene el libro lib1 que vence mañana
                Persona p1 = new Persona(
                        null,
                        "Juan Carlos",
                        "Pérez",
                        32456789,
                        "juan.perez@alumnos.frm.utn.edu.ar",
                        LocalDate.now().minusYears(22), // Cumpleaños HOY
                        d1,
                        new ArrayList<>(List.of(lib1, lib2))
                );

                // p2: Cumpleaños en otra fecha
                Persona p2 = new Persona(
                        null,
                        "María Elena",
                        "Gómez",
                        28765432,
                        "maria.gomez@alumnos.frm.utn.edu.ar",
                        LocalDate.now().minusYears(25).plusMonths(4).plusDays(10),
                        d2,
                        new ArrayList<>(List.of(lib3))
                );

                // p3: Persona sin préstamos activos
                Persona p3 = new Persona(
                        null,
                        "Carlos Alberto",
                        "Ramírez",
                        30123456,
                        "carlos.ramirez@alumnos.frm.utn.edu.ar",
                        LocalDate.now().minusYears(28).minusMonths(2),
                        d3,
                        new ArrayList<>()
                );

                personaRepository.save(p1);
                personaRepository.save(p2);
                personaRepository.save(p3);

                System.out.println(">>> [DATOS INICIALES] Base de datos SQLite inicializada exitosamente.");
                System.out.println(">>> [DATOS INICIALES] Usuario de prueba Juan Carlos Pérez cumple años HOY y tiene el libro 'Cien Años de Soledad' que vence MAÑANA.");
            }
        };
    }
}
