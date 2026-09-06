package com.biblioteca.controller;

import com.biblioteca.model.Libro;
import com.biblioteca.service.AutorService;
import com.biblioteca.service.EditorialService;
import com.biblioteca.service.LibroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

/**
 * ABM/listado de Libros. El listado (GET /libros) lo pueden ver ADMIN y USUARIO
 * (regla configurada en SecurityConfig); el alta/edicion/baja se restringen
 * ademas a nivel de metodo con @PreAuthorize por prolijidad.
 */
@Controller
@RequestMapping("/libros")
@RequiredArgsConstructor
public class LibroController {

    private final LibroService libroService;
    private final AutorService autorService;
    private final EditorialService editorialService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("libros", libroService.listarActivos());
        return "libro/list";
    }

    @GetMapping("/nuevo")
    @PreAuthorize("hasRole('ADMIN')")
    public String formNuevo(Model model) {
        model.addAttribute("libro", new Libro());
        model.addAttribute("autores", autorService.listarActivos());
        model.addAttribute("editoriales", editorialService.listarActivas());
        return "libro/form";
    }

    @GetMapping("/editar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String formEditar(@PathVariable Long id, Model model) {
        model.addAttribute("libro", libroService.buscarPorId(id));
        model.addAttribute("autores", autorService.listarActivos());
        model.addAttribute("editoriales", editorialService.listarActivas());
        return "libro/form";
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasRole('ADMIN')")
    public String guardar(@Valid @ModelAttribute("libro") Libro libro, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("autores", autorService.listarActivos());
            model.addAttribute("editoriales", editorialService.listarActivas());
            return "libro/form";
        }
        libroService.guardar(libro);
        return "redirect:/libros";
    }

    @GetMapping("/baja/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String baja(@PathVariable Long id) {
        libroService.darDeBaja(id);
        return "redirect:/libros";
    }
}
