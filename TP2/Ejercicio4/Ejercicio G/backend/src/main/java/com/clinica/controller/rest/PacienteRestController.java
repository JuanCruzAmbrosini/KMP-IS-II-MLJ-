package com.clinica.controller.rest;

import com.clinica.model.FotoPaciente;
import com.clinica.model.Paciente;
import com.clinica.patterns.facade.ClinicaFacade;
import com.clinica.patterns.facade.PacienteRegistroDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * VERSIÓN B: API REST (Spring Boot REST Controller)
 * Expone endpoints JSON y endpoints multipart para consumo por clientes SPA (React.js).
 */
@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PacienteRestController {

    private final ClinicaFacade clinicaFacade;

    public PacienteRestController(ClinicaFacade clinicaFacade) {
        this.clinicaFacade = clinicaFacade;
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> listarTodos() {
        return ResponseEntity.ok(clinicaFacade.listarPacientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> buscarPorId(@PathVariable String id) {
        return clinicaFacade.buscarPacientePorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint que recibe datos del paciente y archivo binario de FotoPaciente mediante FormData
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registrarPaciente(
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("documento") String documento,
            @RequestParam(value = "foto", required = false) MultipartFile foto) {
        try {
            PacienteRegistroDTO dto = new PacienteRegistroDTO(nombre, apellido, documento);
            byte[] bytes = (foto != null && !foto.isEmpty()) ? foto.getBytes() : null;
            String nombreArchivo = (foto != null && !foto.isEmpty()) ? foto.getOriginalFilename() : null;
            String mime = (foto != null && !foto.isEmpty()) ? foto.getContentType() : null;

            Paciente creado = clinicaFacade.registrarPacienteConFoto(dto, bytes, nombreArchivo, mime);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando imagen: " + e.getMessage());
        }
    }

    @GetMapping("/foto/{id}")
    public ResponseEntity<byte[]> descargarFoto(@PathVariable String id) {
        Optional<FotoPaciente> fotoOpt = clinicaFacade.obtenerFotoPorId(id);
        if (fotoOpt.isPresent()) {
            FotoPaciente foto = fotoOpt.get();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, foto.getMime())
                    .body(foto.getContenido());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarLogico(@PathVariable String id) {
        clinicaFacade.eliminarPaciente(id);
        return ResponseEntity.noContent().build();
    }
}
