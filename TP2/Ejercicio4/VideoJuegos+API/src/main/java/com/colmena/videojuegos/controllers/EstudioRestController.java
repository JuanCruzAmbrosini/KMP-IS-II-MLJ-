package com.colmena.videojuegos.controllers;

import com.colmena.videojuegos.entities.Estudio;
import com.colmena.videojuegos.services.ServicioEstudio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estudios")
@CrossOrigin(origins = "*")
public class EstudioRestController extends BaseController<Estudio, Long, ServicioEstudio> {

    @Autowired
    private ServicioEstudio servicioEstudio;

    @Override
    protected ServicioEstudio getService() {
        return servicioEstudio;
    }
}
