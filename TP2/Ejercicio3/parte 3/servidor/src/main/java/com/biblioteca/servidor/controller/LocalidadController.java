package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.model.Localidad;
import com.biblioteca.servidor.service.LocalidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/localidades")
@CrossOrigin(origins = "*")
public class LocalidadController extends BaseController<Localidad, Long, LocalidadService> {

    @Autowired
    private LocalidadService localidadService;

    @Override
    protected LocalidadService getService() {
        return localidadService;
    }
}
