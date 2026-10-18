package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.service.LibroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/libros")
@CrossOrigin(origins = "*")
public class LibroController extends BaseController<Libro, Long, LibroService> {

    @Autowired
    private LibroService libroService;

    @Override
    protected LibroService getService() {
        return libroService;
    }
}
