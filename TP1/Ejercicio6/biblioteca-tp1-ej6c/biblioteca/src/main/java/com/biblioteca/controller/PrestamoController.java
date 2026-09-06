package com.biblioteca.controller;

import com.biblioteca.model.Prestamo;
import com.biblioteca.service.LibroService;
import com.biblioteca.service.PrestamoService;
import com.biblioteca.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/prestamos")
@RequiredArgsConstructor
public class PrestamoController {

    private final PrestamoService prestamoService;
    private final LibroService libroService;
    private final UsuarioService usuarioService;

    // Listado general -> solo ADMIN (restringido en SecurityConfig)
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("prestamos", prestamoService.listarActivos());
        return "prestamo/list";
    }

    @GetMapping("/nuevo")
    public String formNuevo(Model model) {
        Prestamo prestamo = new Prestamo();
        model.addAttribute("prestamo", prestamo);
        model.addAttribute("libros", libroService.listarActivos());
        return "prestamo/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("prestamo") Prestamo prestamo,
                           @RequestParam Long libroId,
                           Authentication authentication,
                           Model model) {
        prestamo.setLibro(libroService.buscarPorId(libroId));
        prestamo.setUsuario(usuarioService.buscarPorMail(authentication.getName()));
        try {
            prestamoService.registrarPrestamo(prestamo);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("libros", libroService.listarActivos());
            return "prestamo/form";
        }
        return "redirect:/prestamos";
    }

    @GetMapping("/devolucion/{id}")
    public String devolucion(@PathVariable Long id) {
        prestamoService.registrarDevolucion(id);
        return "redirect:/prestamos";
    }
}
