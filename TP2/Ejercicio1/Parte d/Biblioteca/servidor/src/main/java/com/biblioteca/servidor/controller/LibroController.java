package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
@CrossOrigin(origins = "*")
public class LibroController {

    @Autowired
    private LibroService libroService;

    @GetMapping
    public List<Libro> getAll() {
        return libroService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> getById(@PathVariable Long id) {
        return libroService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Libro> create(@RequestBody Libro libro) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.save(libro));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Libro> update(@PathVariable Long id, @RequestBody Libro libro) {
        try {
            return ResponseEntity.ok(libroService.update(id, libro));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ---- Alta / modificacion con PDF (multipart: parte "libro" en JSON + parte "archivo" opcional) ----
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Libro> createConPdf(@RequestPart("libro") Libro libro,
                                              @RequestPart(value = "archivo", required = false) MultipartFile archivo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libroService.saveConPdf(libro, archivo));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Libro> updateConPdf(@PathVariable Long id,
                                              @RequestPart("libro") Libro libro,
                                              @RequestPart(value = "archivo", required = false) MultipartFile archivo) {
        return ResponseEntity.ok(libroService.updateConPdf(id, libro, archivo));
    }

    // ---- Consulta del PDF: se sirve inline para que el navegador lo abra en una pestaña ----
    @GetMapping("/{id}/pdf")
    public ResponseEntity<Resource> getPdf(@PathVariable Long id) {
        Resource pdf = libroService.obtenerPdf(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(pdf.getFilename()).build().toString())
                .body(pdf);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        libroService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

