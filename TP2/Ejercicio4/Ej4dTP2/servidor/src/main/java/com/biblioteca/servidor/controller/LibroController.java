package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.dto.LibroDTO;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.pattern.structural.adapter.LibroAdapter;
import com.biblioteca.servidor.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
@CrossOrigin(origins = "*")
public class LibroController extends BaseController<Libro, Long, LibroService> {

    @Autowired
    private LibroService libroService;

    @Autowired
    private LibroAdapter libroAdapter;

    @Override
    protected LibroService getService() {
        return libroService;
    }

    /**
     * Endpoint del Patrón Estructural Adapter + Creacionales Fábrica y Builder:
     * Recibe un DTO, lo adapta a una entidad polimórfica (LibroFisico o LibroDigital)
     * construida con su respectivo Builder y persistida en base de datos.
     */
    @PostMapping("/dto")
    public ResponseEntity<LibroDTO> crearLibroDTO(@RequestBody LibroDTO dto) {
        Libro guardado = libroService.crearDesdeDto(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(libroAdapter.toDto(guardado));
    }

    /**
     * Endpoint que retorna todos los libros adaptados a LibroDTO.
     */
    @GetMapping("/dto")
    public ResponseEntity<List<LibroDTO>> getAllDTO() {
        List<Libro> libros = libroService.findAll();
        List<LibroDTO> dtos = libros.stream()
                .map(libroAdapter::toDto)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    /**
     * Endpoint que retorna un libro por ID adaptado a LibroDTO.
     */
    @GetMapping("/dto/{id}")
    public ResponseEntity<LibroDTO> getByIdDTO(@PathVariable Long id) {
        return libroService.findById(id)
                .map(libro -> ResponseEntity.ok(libroAdapter.toDto(libro)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Endpoint del Patrón Comportamiento Strategy:
     * Ejecuta la búsqueda de libros alternando la estrategia solicitada:
     * - tipo = "titulo" -> BusquedaPorTituloStrategy
     * - tipo = "genero" -> BusquedaPorGeneroStrategy
     * - tipo = "autor"  -> BusquedaPorAutorStrategy
     *
     * Ejemplo: GET /api/libros/buscar?tipo=titulo&criterio=Soledad
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<LibroDTO>> buscar(
            @RequestParam(defaultValue = "titulo") String tipo,
            @RequestParam(required = false, defaultValue = "") String criterio) {

        List<Libro> encontrados = libroService.buscarConEstrategia(tipo, criterio);
        List<LibroDTO> resultadoDTO = encontrados.stream()
                .map(libroAdapter::toDto)
                .toList();
        return ResponseEntity.ok(resultadoDTO);
    }

    /**
     * Endpoint del Patrón Comportamiento Iterator:
     * Recorre la colección de libros utilizando un LibroIterator personalizado
     * y obtiene los ejemplares que corresponden al autor indicado (nombre o apellido).
     *
     * Ejemplo: GET /api/libros/filtrar-por-autor?autor=García
     */
    @GetMapping("/filtrar-por-autor")
    public ResponseEntity<List<LibroDTO>> filtrarPorAutor(
            @RequestParam String autor) {

        List<Libro> filtrados = libroService.filtrarPorAutorConIterador(autor);
        List<LibroDTO> resultadoDTO = filtrados.stream()
                .map(libroAdapter::toDto)
                .toList();
        return ResponseEntity.ok(resultadoDTO);
    }

    /**
     * Endpoint del Patrón Comportamiento Iterator:
     * Recorre la colección de libros utilizando un LibroIterator personalizado
     * filtrando por el ID de Autor en la relación entidad.
     *
     * Ejemplo: GET /api/libros/filtrar-por-autor-id/1
     */
    @GetMapping("/filtrar-por-autor-id/{autorId}")
    public ResponseEntity<List<LibroDTO>> filtrarPorAutorId(
            @PathVariable Long autorId) {

        List<Libro> filtrados = libroService.filtrarPorAutorIdConIterador(autorId);
        List<LibroDTO> resultadoDTO = filtrados.stream()
                .map(libroAdapter::toDto)
                .toList();
        return ResponseEntity.ok(resultadoDTO);
    }
}
