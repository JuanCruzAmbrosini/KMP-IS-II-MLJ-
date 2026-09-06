package com.biblioteca.controller;

import com.biblioteca.model.Usuario;
import com.biblioteca.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Registracion publica de Usuarios (rol USUARIO). El alta de administradores
 * se hace directamente en la base de datos o por un ADMIN existente.
 */
@Controller
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/usuarios/registro")
    public String formRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registro";
    }

    @PostMapping("/usuarios/registro")
    public String registrar(@Valid @ModelAttribute("usuario") Usuario usuario,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            return "registro";
        }
        try {
            usuarioService.registrar(usuario);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "registro";
        }
        return "redirect:/login?registrado";
    }
}
