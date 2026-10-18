package com.clinica.controller.mvc;

import com.clinica.model.FotoPaciente;
import com.clinica.model.Paciente;
import com.clinica.patterns.facade.ClinicaFacade;
import com.clinica.patterns.facade.PacienteRegistroDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

/**
 * VERSIÓN A: MVC Tradicional (Spring MVC + Thymeleaf)
 * Controlador que procesa peticiones HTTP y renderiza vistas server-side.
 */
@Controller
@RequestMapping("/mvc/pacientes")
public class PacienteWebController {

    private final ClinicaFacade clinicaFacade;

    public PacienteWebController(ClinicaFacade clinicaFacade) {
        this.clinicaFacade = clinicaFacade;
    }

    @GetMapping
    public String listarPacientes(Model model) {
        model.addAttribute("pacientes", clinicaFacade.listarPacientes());
        return "pacientes/lista";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("paciente", new PacienteRegistroDTO());
        return "pacientes/formulario";
    }

    @PostMapping
    public String registrarPaciente(
            @ModelAttribute("paciente") PacienteRegistroDTO dto,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            Model model) {
        try {
            byte[] bytes = (foto != null && !foto.isEmpty()) ? foto.getBytes() : null;
            String nombreArchivo = (foto != null && !foto.isEmpty()) ? foto.getOriginalFilename() : null;
            String mime = (foto != null && !foto.isEmpty()) ? foto.getContentType() : null;

            clinicaFacade.registrarPacienteConFoto(dto, bytes, nombreArchivo, mime);
            return "redirect:/mvc/pacientes";
        } catch (IOException e) {
            model.addAttribute("error", "Error al procesar la foto: " + e.getMessage());
            return "pacientes/formulario";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "pacientes/formulario";
        }
    }

    @GetMapping("/foto/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> verFotoPaciente(@PathVariable String id) {
        Optional<FotoPaciente> fotoOpt = clinicaFacade.obtenerFotoPorId(id);
        if (fotoOpt.isPresent()) {
            FotoPaciente foto = fotoOpt.get();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, foto.getMime())
                    .body(foto.getContenido());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarPaciente(@PathVariable String id) {
        clinicaFacade.eliminarPaciente(id);
        return "redirect:/mvc/pacientes";
    }
}
