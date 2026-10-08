package com.biblioteca.servidor.config;

import com.biblioteca.servidor.model.*;
import com.biblioteca.servidor.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
                // Localidades
                Localidad l1 = localidadRepository.save(new Localidad(null, "Godoy Cruz"));
                Localidad l2 = localidadRepository.save(new Localidad(null, "Ciudad de Mendoza"));
                Localidad l3 = localidadRepository.save(new Localidad(null, "Guaymallén"));

                // Domicilios
                Domicilio d1 = domicilioRepository.save(new Domicilio(null, "San Martín", 1040, l1));
                Domicilio d2 = domicilioRepository.save(new Domicilio(null, "Belgrano", 520, l2));
                Domicilio d3 = domicilioRepository.save(new Domicilio(null, "Colón", 310, l3));

                // Autores
                Autor a1 = autorRepository.save(new Autor(null, "Gabriel", "García Márquez", "Premio Nobel de Literatura 1982"));
                Autor a2 = autorRepository.save(new Autor(null, "Jorge Luis", "Borges", "Escritor y poeta argentino de renombre universal"));
                Autor a3 = autorRepository.save(new Autor(null, "Julio", "Cortázar", "Autor de Rayuela e innovador de la narrativa"));

                // Libros
                Libro lib1 = libroRepository.save(new Libro(
                        null,
                        "Cien Años de Soledad",
                        1967,
                        "Realismo Mágico",
                        417,
                        "Gabriel García Márquez",
                        new ArrayList<>(List.of(a1))
                ));

                Libro lib2 = libroRepository.save(new Libro(
                        null,
                        "Ficciones",
                        1944,
                        "Cuentos / Fantasía",
                        224,
                        "Jorge Luis Borges",
                        new ArrayList<>(List.of(a2))
                ));

                Libro lib3 = libroRepository.save(new Libro(
                        null,
                        "Rayuela",
                        1963,
                        "Novela Experimental",
                        600,
                        "Julio Cortázar",
                        new ArrayList<>(List.of(a3))
                ));

                // Personas
                Persona p1 = new Persona(
                        null,
                        "Juan Carlos",
                        "Pérez",
                        32456789,
                        d1,
                        new ArrayList<>(List.of(lib1, lib2))
                );

                Persona p2 = new Persona(
                        null,
                        "María Elena",
                        "Gómez",
                        28765432,
                        d2,
                        new ArrayList<>(List.of(lib3))
                );

                Persona p3 = new Persona(
                        null,
                        "Carlos Alberto",
                        "Ramírez",
                        30123456,
                        d3,
                        new ArrayList<>(List.of(lib1, lib3))
                );

                personaRepository.save(p1);
                personaRepository.save(p2);
                personaRepository.save(p3);

                System.out.println(">>> Datos iniciales cargados con éxito en la base de datos SQLite.");
            }
        };
    }
}

