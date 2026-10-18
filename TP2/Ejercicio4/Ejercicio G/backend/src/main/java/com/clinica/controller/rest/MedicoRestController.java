package com.clinica.controller.rest;

import com.clinica.model.Medico;
import com.clinica.patterns.facade.ClinicaFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medicos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MedicoRestController {

    private final ClinicaFacade clinicaFacade;

    public MedicoRestController(ClinicaFacade clinicaFacade) {
        this.clinicaFacade = clinicaFacade;
    }

    @GetMapping
    public ResponseEntity<List<Medico>> listarTodos() {
        return ResponseEntity.ok(clinicaFacade.listarMedicos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Medico> buscarPorId(@PathVariable String id) {
        return clinicaFacade.buscarMedicoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
