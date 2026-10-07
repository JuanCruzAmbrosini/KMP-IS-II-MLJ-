package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.dto.LocalidadDTO;
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
    public List<LocalidadDTO> getAll() {
        return localidadService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalidadDTO> getById(@PathVariable Long id) {
        return localidadService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<LocalidadDTO> create(@RequestBody LocalidadDTO localidadDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(localidadService.save(localidadDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocalidadDTO> update(@PathVariable Long id, @RequestBody LocalidadDTO localidadDTO) {
        try {
            return ResponseEntity.ok(localidadService.update(id, localidadDTO));
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
