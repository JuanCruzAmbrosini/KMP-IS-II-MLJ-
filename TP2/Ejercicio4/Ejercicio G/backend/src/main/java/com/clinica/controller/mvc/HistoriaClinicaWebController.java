package com.clinica.controller.mvc;

import com.clinica.model.HistoriaClinica;
import com.clinica.model.Paciente;
import com.clinica.patterns.facade.ClinicaFacade;
import com.clinica.patterns.facade.RegistrarDetalleDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/mvc/historias")
public class HistoriaClinicaWebController {

    private final ClinicaFacade clinicaFacade;

    public HistoriaClinicaWebController(ClinicaFacade clinicaFacade) {
        this.clinicaFacade = clinicaFacade;
    }

    @GetMapping("/paciente/{pacienteId}")
    public String verHistoriaClinica(@PathVariable String pacienteId, Model model) {
        Paciente paciente = clinicaFacade.buscarPacientePorId(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente inexistente: " + pacienteId));
        
        List<HistoriaClinica> historias = clinicaFacade.obtenerHistoriasPorPaciente(pacienteId);

        RegistrarDetalleDTO nuevoDetalle = new RegistrarDetalleDTO();
        nuevoDetalle.setPacienteId(pacienteId);

        model.addAttribute("paciente", paciente);
        model.addAttribute("historias", historias);
        model.addAttribute("medicos", clinicaFacade.listarMedicos());
        model.addAttribute("nuevoDetalle", nuevoDetalle);

        return "historias/ver";
    }

    @PostMapping("/detalle")
    public String agregarDetalle(@ModelAttribute("nuevoDetalle") RegistrarDetalleDTO dto) {
        clinicaFacade.agregarDetalleAHistoria(dto);
        return "redirect:/mvc/historias/paciente/" + dto.getPacienteId();
    }
}
