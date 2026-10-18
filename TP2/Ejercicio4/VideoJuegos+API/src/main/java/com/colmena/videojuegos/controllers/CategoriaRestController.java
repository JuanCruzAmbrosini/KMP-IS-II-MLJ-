package com.colmena.videojuegos.controllers;

import com.colmena.videojuegos.entities.Categoria;
import com.colmena.videojuegos.services.ServicioCategoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaRestController extends BaseController<Categoria, Long, ServicioCategoria> {

    @Autowired
    private ServicioCategoria servicioCategoria;

    @Override
    protected ServicioCategoria getService() {
        return servicioCategoria;
    }
}
