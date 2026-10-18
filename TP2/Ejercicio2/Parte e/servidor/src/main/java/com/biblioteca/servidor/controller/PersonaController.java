package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.model.Persona;
import com.biblioteca.servidor.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin(origins = "*")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @GetMapping
    public List<Persona> getAll() {
        return personaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Persona> getById(@PathVariable Long id) {
        return personaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Persona> create(@RequestBody Persona persona) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personaService.save(persona));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Persona> update(@PathVariable Long id, @RequestBody Persona persona) {
        try {
            return ResponseEntity.ok(personaService.update(id, persona));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

