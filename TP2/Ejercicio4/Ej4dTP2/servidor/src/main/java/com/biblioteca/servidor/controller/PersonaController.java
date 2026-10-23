package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.model.Persona;
import com.biblioteca.servidor.service.PersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin(origins = "*")
public class PersonaController extends BaseController<Persona, Long, PersonaService> {

    @Autowired
    private PersonaService personaService;

    @Override
    protected PersonaService getService() {
        return personaService;
    }
}
