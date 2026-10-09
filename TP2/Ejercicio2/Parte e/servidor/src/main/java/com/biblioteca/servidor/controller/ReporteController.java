package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.Persona;
import com.biblioteca.servidor.service.LibroService;
import com.biblioteca.servidor.service.PersonaService;
import com.biblioteca.servidor.service.ReporteExcelService;
import com.biblioteca.servidor.service.ReportePdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para el Ejercicio 2.d: Generación y descarga de reportes PDF y Excel.
 * Endpoints:
 * - GET /api/reportes/alquileres/pdf : Descarga listado de personas con alquileres en PDF (OpenPDF / iText).
 * - GET /api/reportes/libros-disponibles/excel : Descarga listado de libros disponibles en Excel (Apache POI).
 * - GET /api/reportes/estadisticas : Métricas y contadores de inventario / préstamos.
 * - GET /api/reportes/alquileres/datos : Datos JSON de las personas con préstamos.
 * - GET /api/reportes/libros-disponibles/datos : Datos JSON de los libros disponibles.
 */
@RestController
@RequestMapping("/api/reportes")
@CrossOrigin(origins = "*")
public class ReporteController {

    @Autowired
    private PersonaService personaService;

    @Autowired
    private LibroService libroService;

    @Autowired
    private ReportePdfService reportePdfService;

    @Autowired
    private ReporteExcelService reporteExcelService;

    @GetMapping("/alquileres/pdf")
    public ResponseEntity<byte[]> descargarAlquileresPdf() {
        List<Persona> personasConAlquiler = personaService.findPersonasConAlquileres();
        byte[] pdfBytes = reportePdfService.generarReportePersonasAlquileres(personasConAlquiler);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte_personas_alquileres.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/libros-disponibles/excel")
    public ResponseEntity<byte[]> descargarLibrosDisponiblesExcel() {
        List<Libro> librosDisponibles = libroService.findLibrosDisponibles();
        byte[] excelBytes = reporteExcelService.generarReporteLibrosDisponibles(librosDisponibles);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"libros_disponibles.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @GetMapping("/estadisticas")
    public Map<String, Object> getEstadisticas() {
        List<Persona> todasPersonas = personaService.findAll();
        List<Persona> personasConAlquiler = personaService.findPersonasConAlquileres();
        List<Libro> todosLibros = libroService.findAll();
        List<Libro> librosDisponibles = libroService.findLibrosDisponibles();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPersonas", todasPersonas.size());
        stats.put("personasConAlquiler", personasConAlquiler.size());
        stats.put("totalLibros", todosLibros.size());
        stats.put("librosDisponibles", librosDisponibles.size());
        stats.put("librosAlquilados", Math.max(0, todosLibros.size() - librosDisponibles.size()));
        return stats;
    }

    @GetMapping("/alquileres/datos")
    public List<Persona> getPersonasConAlquileresDatos() {
        return personaService.findPersonasConAlquileres();
    }

    @GetMapping("/libros-disponibles/datos")
    public List<Libro> getLibrosDisponiblesDatos() {
        return libroService.findLibrosDisponibles();
    }
}

