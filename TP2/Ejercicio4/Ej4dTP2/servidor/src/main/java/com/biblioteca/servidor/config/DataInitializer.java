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

                // 3. Autores (Soportan clonación por Patrón Prototype)
                Autor a1 = autorRepository.save(new Autor(null, "Gabriel", "García Márquez", "Premio Nobel de Literatura 1982"));
                Autor a2 = autorRepository.save(new Autor(null, "Jorge Luis", "Borges", "Escritor y poeta argentino de renombre universal"));
                Autor a3 = autorRepository.save(new Autor(null, "Julio", "Cortázar", "Autor de Rayuela e innovador de la narrativa"));

                // 4. Creación de Libros Físicos y Digitales aplicando el Patrón Creacional Builder
                // lib1: Libro Físico - Vence EXACTAMENTE MAÑANA (para recordatorio automático del Ejercicio 2)
                LibroFisico lib1 = LibroFisico.builder()
                        .conTitulo("Cien Años de Soledad")
                        .conFecha(1967)
                        .conGenero("Realismo Mágico")
                        .conPaginas(417)
                        .conAutor("Gabriel García Márquez")
                        .conFechaVencimientoDevolucion(LocalDate.now().plusDays(1))
                        .conAutores(List.of(a1))
                        .conNumeroEstanteria("Estante A-101")
                        .conUbicacion("Sector Novelas Clásicas")
                        .build();
                lib1 = libroRepository.save(lib1);

                // lib2: Libro Digital - Formato PDF
                LibroDigital lib2 = LibroDigital.builder()
                        .conTitulo("Ficciones")
                        .conFecha(1944)
                        .conGenero("Cuentos / Fantasía")
                        .conPaginas(224)
                        .conAutor("Jorge Luis Borges")
                        .conFechaVencimientoDevolucion(LocalDate.now().plusDays(7))
                        .conAutores(List.of(a2))
                        .conFormato("PDF")
                        .conTamanioMB(8.5)
                        .conUrlDescarga("https://biblioteca.frm.utn.edu.ar/ebooks/ficciones.pdf")
                        .build();
                lib2 = libroRepository.save(lib2);

                // lib3: Libro Físico - Vence en 14 días
                LibroFisico lib3 = LibroFisico.builder()
                        .conTitulo("Rayuela")
                        .conFecha(1963)
                        .conGenero("Novela Experimental")
                        .conPaginas(600)
                        .conAutor("Julio Cortázar")
                        .conFechaVencimientoDevolucion(LocalDate.now().plusDays(14))
                        .conAutores(List.of(a3))
                        .conNumeroEstanteria("Estante B-205")
                        .conUbicacion("Sector Vanguardias")
                        .build();
                lib3 = libroRepository.save(lib3);

                // lib4: Libro Digital - Formato EPUB
                LibroDigital lib4 = LibroDigital.builder()
                        .conTitulo("El Aleph")
                        .conFecha(1949)
                        .conGenero("Cuentos / Fantasía")
                        .conPaginas(210)
                        .conAutor("Jorge Luis Borges")
                        .conFechaVencimientoDevolucion(LocalDate.now().plusDays(20))
                        .conAutores(List.of(a2))
                        .conFormato("EPUB")
                        .conTamanioMB(4.2)
                        .conUrlDescarga("https://biblioteca.frm.utn.edu.ar/ebooks/el-aleph.epub")
                        .build();
                lib4 = libroRepository.save(lib4);

                // 5. Personas con emails y fechas de nacimiento
                // p1: Cumpleaños es HOY y tiene libros asignados
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
                        new ArrayList<>(List.of(lib3, lib4))
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

                System.out.println(">>> [DATOS INICIALES - EJERCICIO 4 (d)] Base de datos SQLite inicializada exitosamente.");
                System.out.println(">>> [PATRONES INICIALIZADOS] Libros Físicos y Digitales creados con Builder y Factory.");
                System.out.println(">>> [PATRONES INICIALIZADOS] Autores con soporte de Prototype.");
            }
        };
    }
}
