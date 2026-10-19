package com.biblioteca.servidor;

import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.LibroDigital;
import com.biblioteca.servidor.model.LibroFisico;
import com.biblioteca.servidor.pattern.behavioral.iterator.ColeccionLibros;
import com.biblioteca.servidor.pattern.behavioral.iterator.ColeccionLibrosImpl;
import com.biblioteca.servidor.pattern.behavioral.iterator.LibroIterator;
import com.biblioteca.servidor.pattern.behavioral.iterator.LibroPorAutorIterator;
import com.biblioteca.servidor.pattern.behavioral.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Patrones de Comportamiento: Strategy e Iterator")
class BehavioralPatternsTest {

    private List<Libro> catalogo;
    private Autor gabrielGarcia;
    private Autor jorgeBorges;
    private Autor julioCortazar;

    @BeforeEach
    void setUp() {
        gabrielGarcia = new Autor(1L, "Gabriel", "García Márquez", "Premio Nobel");
        jorgeBorges = new Autor(2L, "Jorge Luis", "Borges", "Escritor universal");
        julioCortazar = new Autor(3L, "Julio", "Cortázar", "Novelista y cuentista");

        Libro l1 = LibroFisico.builder()
                .conId(1L)
                .conTitulo("Cien Años de Soledad")
                .conGenero("Realismo Mágico")
                .conFecha(1967)
                .conPaginas(417)
                .conAutor("Gabriel García Márquez")
                .conAutores(List.of(gabrielGarcia))
                .conNumeroEstanteria("A-1")
                .build();

        Libro l2 = LibroDigital.builder()
                .conId(2L)
                .conTitulo("El Amor en los Tiempos del Cólera")
                .conGenero("Novela Romántica")
                .conFecha(1985)
                .conPaginas(368)
                .conAutor("Gabriel García Márquez")
                .conAutores(List.of(gabrielGarcia))
                .conFormato("EPUB")
                .build();

        Libro l3 = LibroDigital.builder()
                .conId(3L)
                .conTitulo("Ficciones")
                .conGenero("Cuentos / Fantasía")
                .conFecha(1944)
                .conPaginas(224)
                .conAutor("Jorge Luis Borges")
                .conAutores(List.of(jorgeBorges))
                .conFormato("PDF")
                .build();

        Libro l4 = LibroFisico.builder()
                .conId(4L)
                .conTitulo("Rayuela")
                .conGenero("Novela Experimental")
                .conFecha(1963)
                .conPaginas(600)
                .conAutor("Julio Cortázar")
                .conAutores(List.of(julioCortazar))
                .conNumeroEstanteria("B-2")
                .build();

        catalogo = List.of(l1, l2, l3, l4);
    }

    // ---------------------------------------------------------
    // Pruebas del Patrón Strategy
    // ---------------------------------------------------------

    @Test
    @DisplayName("Strategy: Búsqueda por Título (BusquedaPorTituloStrategy)")
    void testStrategyBusquedaPorTitulo() {
        BusquedaLibroStrategy strategy = new BusquedaPorTituloStrategy();

        List<Libro> resultado = strategy.buscar(catalogo, "Amor");
        assertEquals(1, resultado.size());
        assertEquals("El Amor en los Tiempos del Cólera", resultado.get(0).getTitulo());

        List<Libro> sinResultados = strategy.buscar(catalogo, "Inexistente");
        assertTrue(sinResultados.isEmpty());
    }

    @Test
    @DisplayName("Strategy: Búsqueda por Género (BusquedaPorGeneroStrategy)")
    void testStrategyBusquedaPorGenero() {
        BusquedaLibroStrategy strategy = new BusquedaPorGeneroStrategy();

        List<Libro> fantasia = strategy.buscar(catalogo, "fantasía");
        assertEquals(1, fantasia.size());
        assertEquals("Ficciones", fantasia.get(0).getTitulo());

        List<Libro> novelas = strategy.buscar(catalogo, "Novela");
        assertEquals(2, novelas.size()); // Novela Romántica y Novela Experimental
    }

    @Test
    @DisplayName("Strategy: Búsqueda por Autor (BusquedaPorAutorStrategy)")
    void testStrategyBusquedaPorAutor() {
        BusquedaLibroStrategy strategy = new BusquedaPorAutorStrategy();

        List<Libro> borges = strategy.buscar(catalogo, "Borges");
        assertEquals(1, borges.size());
        assertEquals("Ficciones", borges.get(0).getTitulo());

        List<Libro> garcia = strategy.buscar(catalogo, "García Márquez");
        assertEquals(2, garcia.size());
    }

    @Test
    @DisplayName("Strategy: Alternancia dinámica de algoritmos en BusquedaLibroContext")
    void testBusquedaLibroContextDynamicSwitch() {
        BusquedaLibroContext context = new BusquedaLibroContext(List.of(
                new BusquedaPorTituloStrategy(),
                new BusquedaPorGeneroStrategy(),
                new BusquedaPorAutorStrategy()
        ));

        // 1. Ejecutar con Estrategia de Título
        List<Libro> porTitulo = context.ejecutarCon("TITULO", catalogo, "Rayuela");
        assertEquals(1, porTitulo.size());
        assertEquals("Rayuela", porTitulo.get(0).getTitulo());

        // 2. Cambiar dinámicamente a Estrategia de Autor
        List<Libro> porAutor = context.ejecutarCon("AUTOR", catalogo, "Cortázar");
        assertEquals(1, porAutor.size());
        assertEquals("Rayuela", porAutor.get(0).getTitulo());

        // 3. Cambiar dinámicamente a Estrategia de Género
        List<Libro> porGenero = context.ejecutarCon("GENERO", catalogo, "Mágico");
        assertEquals(1, porGenero.size());
        assertEquals("Cien Años de Soledad", porGenero.get(0).getTitulo());
    }

    // ---------------------------------------------------------
    // Pruebas del Patrón Iterator
    // ---------------------------------------------------------

    @Test
    @DisplayName("Iterator: Recorrido y filtrado de libros de un autor por nombre con LibroPorAutorIterator")
    void testLibroPorAutorIteratorPorNombre() {
        ColeccionLibros coleccion = new ColeccionLibrosImpl(catalogo);
        LibroIterator iterator = coleccion.crearIteradorPorAutor("Gabriel García Márquez");

        assertTrue(iterator.hasNext());
        Libro libro1 = iterator.next();
        assertEquals("Cien Años de Soledad", libro1.getTitulo());

        assertTrue(iterator.hasNext());
        Libro libro2 = iterator.next();
        assertEquals("El Amor en los Tiempos del Cólera", libro2.getTitulo());

        assertFalse(iterator.hasNext(), "No deben quedar más libros de García Márquez");
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    @DisplayName("Iterator: Recorrido y filtrado de libros por Autor ID con LibroPorAutorIterator")
    void testLibroPorAutorIteratorPorId() {
        ColeccionLibros coleccion = new ColeccionLibrosImpl(catalogo);
        LibroIterator iterator = coleccion.crearIteradorPorAutorId(2L); // ID de Jorge Luis Borges

        assertTrue(iterator.hasNext());
        Libro libro = iterator.next();
        assertEquals("Ficciones", libro.getTitulo());

        assertFalse(iterator.hasNext());
    }

    @Test
    @DisplayName("Iterator: Recolección completa con helper recolectarTodos()")
    void testLibroPorAutorIteratorRecolectarTodos() {
        LibroPorAutorIterator iterator = new LibroPorAutorIterator(catalogo, "Cortázar");
        List<Libro> librosCortazar = iterator.recolectarTodos();

        assertEquals(1, librosCortazar.size());
        assertEquals("Rayuela", librosCortazar.get(0).getTitulo());
    }
}

