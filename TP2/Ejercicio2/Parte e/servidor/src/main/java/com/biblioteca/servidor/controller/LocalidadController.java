package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.model.Localidad;
import com.biblioteca.servidor.service.LocalidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/localidades")
@CrossOrigin(origins = "*")
public class LocalidadController {

    @Autowired
    private LocalidadService localidadService;

    @GetMapping
    public List<Localidad> getAll() {
        return localidadService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Localidad> getById(@PathVariable Long id) {
        return localidadService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Localidad> create(@RequestBody Localidad localidad) {
        return ResponseEntity.status(HttpStatus.CREATED).body(localidadService.save(localidad));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Localidad> update(@PathVariable Long id, @RequestBody Localidad localidad) {
        try {
            return ResponseEntity.ok(localidadService.update(id, localidad));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        localidadService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

