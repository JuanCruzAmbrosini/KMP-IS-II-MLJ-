package com.colmena.videojuegos.controllers;

import com.colmena.videojuegos.entities.Videojuego;
import com.colmena.videojuegos.services.ServicioVideojuego;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/videojuegos")
@CrossOrigin(origins = "*")
public class VideojuegoRestController extends BaseController<Videojuego, Long, ServicioVideojuego> {

    @Autowired
    private ServicioVideojuego servicioVideojuego;

    @Override
    protected ServicioVideojuego getService() {
        return servicioVideojuego;
    }

    // --- Sobrescritura de Hook del Template Method ---

    @Override
    protected void validarEntidad(Videojuego entity) {
        if (entity.getTitulo() == null || entity.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del videojuego no puede estar vacío");
        }
    }

    // --- Endpoints adicionales propios del dominio Videojuego ---

    @GetMapping("/activos")
    public ResponseEntity<List<Videojuego>> getAllActivos() {
        return ResponseEntity.ok(servicioVideojuego.findAllByActivo());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Videojuego>> buscarPorTitulo(@RequestParam("query") String query) {
        return ResponseEntity.ok(servicioVideojuego.findByTitle(query));
    }
}
