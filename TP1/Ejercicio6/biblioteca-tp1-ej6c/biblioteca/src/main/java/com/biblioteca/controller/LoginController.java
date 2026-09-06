package com.biblioteca.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de la pantalla de login. La autenticacion en si
 * la resuelve Spring Security (loginProcessingUrl = /login) via formLogin.
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html
    }

    @GetMapping("/")
    public String home() {
        return "index"; // templates/index.html
    }
}
