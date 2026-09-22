package com.colmena.videojuegos.controllers;

import com.colmena.videojuegos.repositories.RepositorioAuditoriaVideojuego;
import com.colmena.videojuegos.services.ServicioVideojuego;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ControladorAuditoria {
    private final ServicioVideojuego servicioVideojuego;
    private final RepositorioAuditoriaVideojuego repositorioAuditoria;

    public ControladorAuditoria(
            ServicioVideojuego servicioVideojuego,
            RepositorioAuditoriaVideojuego repositorioAuditoria) {
        this.servicioVideojuego = servicioVideojuego;
        this.repositorioAuditoria = repositorioAuditoria;
    }

    @GetMapping("/auditoria")
    public String auditoria(Model model) {
        try {
            var auditorias = repositorioAuditoria.findAllByOrderByFechaDescIdDesc();
            model.addAttribute("auditorias", auditorias);
            model.addAttribute("totalAuditorias", auditorias.size());
            return "views/auditoria";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
}