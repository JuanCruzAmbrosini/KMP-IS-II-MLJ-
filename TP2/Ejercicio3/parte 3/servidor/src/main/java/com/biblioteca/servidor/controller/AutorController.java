package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.service.AutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/autores")
@CrossOrigin(origins = "*")
public class AutorController extends BaseController<Autor, Long, AutorService> {

    @Autowired
    private AutorService autorService;

    @Override
    protected AutorService getService() {
        return autorService;
    }
}
