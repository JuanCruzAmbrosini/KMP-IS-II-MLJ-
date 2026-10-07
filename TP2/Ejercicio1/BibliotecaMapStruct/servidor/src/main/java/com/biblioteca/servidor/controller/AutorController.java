package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.dto.AutorDTO;
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
    public List<AutorDTO> getAll() {
        return autorService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AutorDTO> getById(@PathVariable Long id) {
        return autorService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AutorDTO> create(@RequestBody AutorDTO autorDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(autorService.save(autorDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutorDTO> update(@PathVariable Long id, @RequestBody AutorDTO autorDTO) {
        try {
            return ResponseEntity.ok(autorService.update(id, autorDTO));
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
