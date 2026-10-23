package com.biblioteca.servidor;

import com.biblioteca.servidor.dto.AutorDTO;
import com.biblioteca.servidor.dto.LibroDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de Integración de Endpoints REST con Patrones GOF")
class PatternIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Endpoint Factory/Builder/Adapter: Creación de Libro Físico vía DTO")
    void testCrearLibroFisicoDTO() throws Exception {
        LibroDTO dto = new LibroDTO();
        dto.setTitulo("Don Quijote de la Mancha");
        dto.setFecha(1605);
        dto.setGenero("Clásico");
        dto.setPaginas(863);
        dto.setAutor("Miguel de Cervantes");
        dto.setTipoLibro("FISICO");
        dto.setNumeroEstanteria("E-99");
        dto.setUbicacion("Sala Histórica");

        mockMvc.perform(post("/api/libros/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.titulo", is("Don Quijote de la Mancha")))
                .andExpect(jsonPath("$.tipoLibro", is("FISICO")))
                .andExpect(jsonPath("$.numeroEstanteria", is("E-99")))
                .andExpect(jsonPath("$.ubicacion", is("Sala Histórica")));
    }

    @Test
    @DisplayName("Endpoint Factory/Builder/Adapter: Creación de Libro Digital vía DTO")
    void testCrearLibroDigitalDTO() throws Exception {
        LibroDTO dto = new LibroDTO();
        dto.setTitulo("El Arte de la Guerra");
        dto.setFecha(1910);
        dto.setGenero("Estrategia");
        dto.setPaginas(120);
        dto.setAutor("Sun Tzu");
        dto.setTipoLibro("DIGITAL");
        dto.setFormato("EPUB");
        dto.setTamanioMB(2.5);
        dto.setUrlDescarga("https://biblioteca.test/sun-tzu.epub");

        mockMvc.perform(post("/api/libros/dto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.titulo", is("El Arte de la Guerra")))
                .andExpect(jsonPath("$.tipoLibro", is("DIGITAL")))
                .andExpect(jsonPath("$.formato", is("EPUB")))
                .andExpect(jsonPath("$.tamanioMB", is(2.5)));
    }

    @Test
    @DisplayName("Endpoint Prototype: Clonación de Autor vía POST /api/autores/{id}/clonar")
    void testClonarAutorEndpoint() throws Exception {
        AutorDTO modificaciones = new AutorDTO();
        modificaciones.setNombre("Jorge Luis");
        modificaciones.setApellido("Borges (Copia para Archivo Histórico)");
        modificaciones.setBiografia("Edición especial de homenaje");

        mockMvc.perform(post("/api/autores/2/clonar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modificaciones)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.apellido", containsString("Copia para Archivo Histórico")))
                .andExpect(jsonPath("$.biografia", is("Edición especial de homenaje")));
    }

    @Test
    @DisplayName("Endpoint Strategy: Búsqueda dinámica por Título, Género y Autor")
    void testBusquedaEstrategias() throws Exception {
        // 1. Por Título
        mockMvc.perform(get("/api/libros/buscar")
                        .param("tipo", "titulo")
                        .param("criterio", "Soledad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].titulo", containsString("Cien Años de Soledad")));

        // 2. Por Género
        mockMvc.perform(get("/api/libros/buscar")
                        .param("tipo", "genero")
                        .param("criterio", "Fantasía"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].genero", containsString("Fantasía")));

        // 3. Por Autor
        mockMvc.perform(get("/api/libros/buscar")
                        .param("tipo", "autor")
                        .param("criterio", "Borges"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Endpoint Iterator: Filtrado de libros por Autor mediante LibroPorAutorIterator")
    void testFiltrarPorAutorIterator() throws Exception {
        mockMvc.perform(get("/api/libros/filtrar-por-autor")
                        .param("autor", "Cortázar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].titulo", is("Rayuela")));
    }
}

