package com.biblioteca.controller;

import com.biblioteca.model.Editorial;
import com.biblioteca.service.EditorialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/editoriales")
@RequiredArgsConstructor
public class EditorialController {

    private final EditorialService editorialService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("editoriales", editorialService.listarActivas());
        return "editorial/list";
    }

    @GetMapping("/nuevo")
    public String formNuevo(Model model) {
        model.addAttribute("editorial", new Editorial());
        return "editorial/form";
    }

    @GetMapping("/editar/{id}")
    public String formEditar(@PathVariable Long id, Model model) {
        model.addAttribute("editorial", editorialService.buscarPorId(id));
        return "editorial/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("editorial") Editorial editorial, BindingResult result) {
        if (result.hasErrors()) {
            return "editorial/form";
        }
        editorialService.guardar(editorial);
        return "redirect:/editoriales";
    }

    @GetMapping("/baja/{id}")
    public String baja(@PathVariable Long id) {
        editorialService.darDeBaja(id);
        return "redirect:/editoriales";
    }
}
