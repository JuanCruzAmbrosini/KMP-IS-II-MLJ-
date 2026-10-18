package com.clinica.controller.rest;

import com.clinica.model.HistoriaClinica;
import com.clinica.patterns.facade.ClinicaFacade;
import com.clinica.patterns.facade.RegistrarDetalleDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/historias-clinicas")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HistoriaClinicaRestController {

    private final ClinicaFacade clinicaFacade;

    public HistoriaClinicaRestController(ClinicaFacade clinicaFacade) {
        this.clinicaFacade = clinicaFacade;
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<HistoriaClinica>> obtenerPorPaciente(@PathVariable String pacienteId) {
        List<HistoriaClinica> historias = clinicaFacade.obtenerHistoriasPorPaciente(pacienteId);
        return ResponseEntity.ok(historias);
    }

    @PostMapping("/detalles")
    public ResponseEntity<?> agregarDetalle(@RequestBody RegistrarDetalleDTO dto) {
        try {
            HistoriaClinica actualizada = clinicaFacade.agregarDetalleAHistoria(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(actualizada);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error en servidor: " + e.getMessage());
        }
    }
}
