package com.biblioteca.servidor;

import com.biblioteca.servidor.dto.LibroDTO;
import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.LibroDigital;
import com.biblioteca.servidor.model.LibroFisico;
import com.biblioteca.servidor.pattern.creational.builder.LibroDigitalBuilder;
import com.biblioteca.servidor.pattern.creational.builder.LibroFisicoBuilder;
import com.biblioteca.servidor.pattern.creational.factory.LibroDigitalFactory;
import com.biblioteca.servidor.pattern.creational.factory.LibroFactoryProvider;
import com.biblioteca.servidor.pattern.creational.factory.LibroFisicoFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Patrones Creacionales: Builder, Factory y Prototype")
class CreationalPatternsTest {

    @Test
    @DisplayName("Builder: Construcción correcta de LibroFisico con LibroFisicoBuilder")
    void testLibroFisicoBuilder() {
        Autor autor = new Autor(1L, "Julio", "Cortázar", "Escritor argentino");

        LibroFisico libro = new LibroFisicoBuilder()
                .conId(10L)
                .conTitulo("Rayuela")
                .conFecha(1963)
                .conGenero("Novela")
                .conPaginas(600)
                .conAutor("Julio Cortázar")
                .conFechaVencimientoDevolucion(LocalDate.of(2026, 12, 31))
                .conNumeroEstanteria("Estante E-4")
                .conUbicacion("Piso 2, Sala A")
                .agregarAutor(autor)
                .build();

        assertNotNull(libro);
        assertEquals(10L, libro.getId());
        assertEquals("Rayuela", libro.getTitulo());
        assertEquals(1963, libro.getFecha());
        assertEquals("Novela", libro.getGenero());
        assertEquals(600, libro.getPaginas());
        assertEquals("FISICO", libro.getTipoLibro());
        assertEquals("Estante E-4", libro.getNumeroEstanteria());
        assertEquals("Piso 2, Sala A", libro.getUbicacion());
        assertEquals(1, libro.getAutores().size());
        assertEquals("Cortázar", libro.getAutores().get(0).getApellido());
    }

    @Test
    @DisplayName("Builder: Construcción correcta de LibroDigital con LibroDigitalBuilder")
    void testLibroDigitalBuilder() {
        LibroDigital libro = new LibroDigitalBuilder()
                .conTitulo("Clean Code")
                .conFecha(2008)
                .conGenero("Tecnología")
                .conPaginas(464)
                .conAutor("Robert C. Martin")
                .conFormato("EPUB")
                .conTamanioMB(12.4)
                .conUrlDescarga("https://ejemplo.com/clean-code.epub")
                .build();

        assertNotNull(libro);
        assertEquals("Clean Code", libro.getTitulo());
        assertEquals("DIGITAL", libro.getTipoLibro());
        assertEquals("EPUB", libro.getFormato());
        assertEquals(12.4, libro.getTamanioMB());
        assertEquals("https://ejemplo.com/clean-code.epub", libro.getUrlDescarga());
    }

    @Test
    @DisplayName("Factory: Fabricación de LibroFisico y LibroDigital mediante LibroFactoryProvider")
    void testLibroFactoryProvider() {
        LibroFisicoFactory fisicoFactory = new LibroFisicoFactory();
        LibroDigitalFactory digitalFactory = new LibroDigitalFactory();
        LibroFactoryProvider provider = new LibroFactoryProvider(List.of(fisicoFactory, digitalFactory));

        // 1. DTO de Libro Físico
        LibroDTO dtoFisico = new LibroDTO();
        dtoFisico.setTitulo("Ficciones");
        dtoFisico.setTipoLibro("FISICO");
        dtoFisico.setNumeroEstanteria("B-12");
        dtoFisico.setUbicacion("Sector Literatura");

        Libro libroFisico = provider.crear(dtoFisico);
        assertInstanceOf(LibroFisico.class, libroFisico);
        assertEquals("Ficciones", libroFisico.getTitulo());
        assertEquals("B-12", ((LibroFisico) libroFisico).getNumeroEstanteria());

        // 2. DTO de Libro Digital
        LibroDTO dtoDigital = new LibroDTO();
        dtoDigital.setTitulo("El Aleph");
        dtoDigital.setTipoLibro("DIGITAL");
        dtoDigital.setFormato("PDF");
        dtoDigital.setTamanioMB(4.8);

        Libro libroDigital = provider.crear(dtoDigital);
        assertInstanceOf(LibroDigital.class, libroDigital);
        assertEquals("El Aleph", libroDigital.getTitulo());
        assertEquals("PDF", ((LibroDigital) libroDigital).getFormato());
        assertEquals(4.8, ((LibroDigital) libroDigital).getTamanioMB());
    }

    @Test
    @DisplayName("Prototype: Clonación de Autor mediante clonar() y clonarConModificaciones()")
    void testAutorPrototypeCloning() {
        Autor autorOriginal = new Autor(1L, "Gabriel", "García Márquez", "Premio Nobel 1982");

        // Clonación exacta
        Autor clon1 = autorOriginal.clonar();
        assertNotNull(clon1);
        assertNotSame(autorOriginal, clon1, "El clon debe ser una instancia nueva en memoria");
        assertNull(clon1.getId(), "El clon no debe heredar el ID del original para permitir inserción nueva");
        assertEquals(autorOriginal.getNombre(), clon1.getNombre());
        assertEquals(autorOriginal.getApellido(), clon1.getApellido());
        assertEquals(autorOriginal.getBiografia(), clon1.getBiografia());

        // Clonación con modificaciones
        Autor clon2 = autorOriginal.clonarConModificaciones("Mercedes", "Barcha", "Esposa y promotora cultural");
        assertNotNull(clon2);
        assertNotSame(autorOriginal, clon2);
        assertEquals("Mercedes", clon2.getNombre());
        assertEquals("Barcha", clon2.getApellido());
        assertEquals("Esposa y promotora cultural", clon2.getBiografia());
    }
}

