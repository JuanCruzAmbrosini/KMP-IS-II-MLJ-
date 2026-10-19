package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.dto.AutorDTO;
import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.pattern.structural.adapter.AutorAdapter;
import com.biblioteca.servidor.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/autores")
@CrossOrigin(origins = "*")
public class AutorController extends BaseController<Autor, Long, AutorService> {

    @Autowired
    private AutorService autorService;

    @Autowired
    private AutorAdapter autorAdapter;

    @Override
    protected AutorService getService() {
        return autorService;
    }

    /**
     * Endpoint del Patrón Creacional Prototype:
     * Clona el autor indicado por su ID, permitiendo opcionalmente sobrescribir
     * su nombre, apellido o biografía recibidos en el payload DTO.
     */
    @PostMapping("/{id}/clonar")
    public ResponseEntity<AutorDTO> clonarAutor(
            @PathVariable Long id,
            @RequestBody(required = false) AutorDTO modificaciones) {

        String nuevoNombre = modificaciones != null ? modificaciones.getNombre() : null;
        String nuevoApellido = modificaciones != null ? modificaciones.getApellido() : null;
        String nuevaBiografia = modificaciones != null ? modificaciones.getBiografia() : null;

        try {
            Autor clonado = autorService.clonarAutor(id, nuevoNombre, nuevoApellido, nuevaBiografia);
            return ResponseEntity.status(HttpStatus.CREATED).body(autorAdapter.toDto(clonado));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
