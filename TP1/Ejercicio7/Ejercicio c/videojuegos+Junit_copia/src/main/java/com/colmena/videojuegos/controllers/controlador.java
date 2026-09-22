package com.colmena.videojuegos.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class controlador {
    @GetMapping(value = "/")
    public String index() {
        return "redirect:/inicio";
    }
}
