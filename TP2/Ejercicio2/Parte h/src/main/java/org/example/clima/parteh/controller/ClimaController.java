package org.example.clima.parteh.controller;

import org.example.clima.parteh.service.ClimaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ciudad")
public class ClimaController {

    private final ClimaService climaService;

    public ClimaController(ClimaService climaService) {
        this.climaService = climaService;
    }

    @GetMapping("/clima/{ciudad}")
    public ResponseEntity<String> getClima(@PathVariable String ciudad) {
        return climaService.getClima(ciudad);
    }

}
