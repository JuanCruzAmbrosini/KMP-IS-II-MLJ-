package com.biblioteca.servidor;

import com.biblioteca.servidor.dto.*;
import com.biblioteca.servidor.model.*;
import com.biblioteca.servidor.pattern.creational.factory.LibroDigitalFactory;
import com.biblioteca.servidor.pattern.creational.factory.LibroFactoryProvider;
import com.biblioteca.servidor.pattern.creational.factory.LibroFisicoFactory;
import com.biblioteca.servidor.pattern.structural.adapter.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Patrón Estructural Adapter")
class StructuralAdapterPatternTest {

    private AutorAdapter autorAdapter;
    private LibroAdapter libroAdapter;
    private LocalidadAdapter localidadAdapter;
    private DomicilioAdapter domicilioAdapter;
    private PersonaAdapter personaAdapter;

    @BeforeEach
    void setUp() {
        autorAdapter = new AutorAdapter();

        LibroFisicoFactory fisicoFactory = new LibroFisicoFactory();
        LibroDigitalFactory digitalFactory = new LibroDigitalFactory();
        LibroFactoryProvider factoryProvider = new LibroFactoryProvider(List.of(fisicoFactory, digitalFactory));

        libroAdapter = new LibroAdapter();
        ReflectionTestUtils.setField(libroAdapter, "autorAdapter", autorAdapter);
        ReflectionTestUtils.setField(libroAdapter, "libroFactoryProvider", factoryProvider);

        localidadAdapter = new LocalidadAdapter();
        domicilioAdapter = new DomicilioAdapter();
        ReflectionTestUtils.setField(domicilioAdapter, "localidadAdapter", localidadAdapter);

        personaAdapter = new PersonaAdapter();
        ReflectionTestUtils.setField(personaAdapter, "domicilioAdapter", domicilioAdapter);
        ReflectionTestUtils.setField(personaAdapter, "libroAdapter", libroAdapter);
    }

    @Test
    @DisplayName("Adapter: Adaptación bidireccional Autor <-> AutorDTO")
    void testAutorAdapter() {
        Autor autor = new Autor(5L, "Jorge Luis", "Borges", "Escritor argentino");

        AutorDTO dto = autorAdapter.toDto(autor);
        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals("Jorge Luis", dto.getNombre());
        assertEquals("Borges", dto.getApellido());

        Autor entityReconvertida = autorAdapter.toEntity(dto);
        assertNotNull(entityReconvertida);
        assertEquals(dto.getId(), entityReconvertida.getId());
        assertEquals(dto.getNombre(), entityReconvertida.getNombre());
        assertEquals(dto.getApellido(), entityReconvertida.getApellido());
    }

    @Test
    @DisplayName("Adapter: Adaptación bidireccional LibroFisico <-> LibroDTO")
    void testLibroFisicoAdapter() {
        Autor autor = new Autor(1L, "Gabriel", "García Márquez", "Premio Nobel");
        LibroFisico fisico = LibroFisico.builder()
                .conId(20L)
                .conTitulo("Cien Años de Soledad")
                .conFecha(1967)
                .conGenero("Realismo Mágico")
                .conPaginas(417)
                .conAutor("Gabriel García Márquez")
                .conNumeroEstanteria("E-1")
                .conUbicacion("Piso 1")
                .conAutores(List.of(autor))
                .build();

        LibroDTO dto = libroAdapter.toDto(fisico);
        assertNotNull(dto);
        assertEquals("FISICO", dto.getTipoLibro());
        assertEquals("E-1", dto.getNumeroEstanteria());
        assertEquals("Piso 1", dto.getUbicacion());
        assertEquals(1, dto.getAutores().size());

        Libro entityReconvertida = libroAdapter.toEntity(dto);
        assertInstanceOf(LibroFisico.class, entityReconvertida);
        assertEquals("Cien Años de Soledad", entityReconvertida.getTitulo());
        assertEquals("E-1", ((LibroFisico) entityReconvertida).getNumeroEstanteria());
    }

    @Test
    @DisplayName("Adapter: Adaptación bidireccional LibroDigital <-> LibroDTO")
    void testLibroDigitalAdapter() {
        LibroDigital digital = LibroDigital.builder()
                .conId(30L)
                .conTitulo("Ficciones")
                .conFecha(1944)
                .conGenero("Fantasía")
                .conPaginas(200)
                .conAutor("Jorge Luis Borges")
                .conFormato("PDF")
                .conTamanioMB(6.5)
                .conUrlDescarga("https://biblioteca.test/ficciones.pdf")
                .build();

        LibroDTO dto = libroAdapter.toDto(digital);
        assertNotNull(dto);
        assertEquals("DIGITAL", dto.getTipoLibro());
        assertEquals("PDF", dto.getFormato());
        assertEquals(6.5, dto.getTamanioMB());

        Libro entityReconvertida = libroAdapter.toEntity(dto);
        assertInstanceOf(LibroDigital.class, entityReconvertida);
        assertEquals("PDF", ((LibroDigital) entityReconvertida).getFormato());
    }

    @Test
    @DisplayName("Adapter: Adaptación bidireccional Persona <-> PersonaDTO")
    void testPersonaAdapter() {
        Localidad loc = new Localidad(1L, "Godoy Cruz");
        Domicilio dom = new Domicilio(1L, "San Martín", 1040, "-32.88", "-68.84", loc);
        Persona p = new Persona(100L, "Juan", "Pérez", 30123456, "juan@test.com",
                LocalDate.of(2000, 5, 20), dom, List.of());

        PersonaDTO dto = personaAdapter.toDto(p);
        assertNotNull(dto);
        assertEquals("Juan", dto.getNombre());
        assertEquals("Godoy Cruz", dto.getDomicilio().getLocalidad().getDenominacion());

        Persona entity = personaAdapter.toEntity(dto);
        assertNotNull(entity);
        assertEquals(dto.getNombre(), entity.getNombre());
        assertEquals(dto.getDni(), entity.getDni());
    }
}

