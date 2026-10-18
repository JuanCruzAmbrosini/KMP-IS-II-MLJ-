package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/autores")
@CrossOrigin(origins = "*")
public class AutorController {

    @Autowired
    private AutorService autorService;

    @GetMapping
    public List<Autor> getAll() {
        return autorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Autor> getById(@PathVariable Long id) {
        return autorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Autor> create(@RequestBody Autor autor) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autorService.save(autor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Autor> update(@PathVariable Long id, @RequestBody Autor autor) {
        try {
            return ResponseEntity.ok(autorService.update(id, autor));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        autorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

