package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.service.AutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

// Solo ADMIN puede llegar aca (restringido en SecurityConfig por /autores/**)
@Controller
@RequestMapping("/autores")
@RequiredArgsConstructor
public class AutorController {

    private final AutorService autorService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("autores", autorService.listarActivos());
        return "autor/list";
    }

    @GetMapping("/nuevo")
    public String formNuevo(Model model) {
        model.addAttribute("autor", new Autor());
        return "autor/form";
    }

    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable Long id, Model model) {
        model.addAttribute("autor", autorService.buscarPorId(id));
        return "autor/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("autor") Autor autor, BindingResult result) {
        if (result.hasErrors()) {
            return "autor/form";
        }
        autorService.guardar(autor);
        return "redirect:/autores";
    }

    @GetMapping("/baja/{id}")
    public String baja(@PathVariable Long id) {
        autorService.darDeBaja(id);
        return "redirect:/autores";
    }
}
