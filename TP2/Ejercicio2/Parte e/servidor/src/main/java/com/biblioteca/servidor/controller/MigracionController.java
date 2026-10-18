package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.service.MigracionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/migracion")
@CrossOrigin(origins = "*")
public class MigracionController {

    private final MigracionService migracionService;

    public MigracionController(MigracionService migracionService) {
        this.migracionService = migracionService;
    }

    @PostMapping
    public ResponseEntity<MigracionService.MigrationResult> importar() throws IOException {
        return ResponseEntity.ok(migracionService.importar());
    }
}
