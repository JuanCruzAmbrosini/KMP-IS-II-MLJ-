package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.dto.DomicilioDTO;
import com.biblioteca.servidor.service.DomicilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domicilios")
@CrossOrigin(origins = "*")
public class DomicilioController {

    @Autowired
    private DomicilioService domicilioService;

    @GetMapping
    public List<DomicilioDTO> getAll() {
        return domicilioService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DomicilioDTO> getById(@PathVariable Long id) {
        return domicilioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DomicilioDTO> create(@RequestBody DomicilioDTO domicilioDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(domicilioService.save(domicilioDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DomicilioDTO> update(@PathVariable Long id, @RequestBody DomicilioDTO domicilioDTO) {
        try {
            return ResponseEntity.ok(domicilioService.update(id, domicilioDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        domicilioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
