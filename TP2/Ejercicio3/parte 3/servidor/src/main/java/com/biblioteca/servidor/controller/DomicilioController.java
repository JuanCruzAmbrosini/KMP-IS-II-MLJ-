package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.model.Domicilio;
import com.biblioteca.servidor.service.DomicilioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/domicilios")
@CrossOrigin(origins = "*")
public class DomicilioController extends BaseController<Domicilio, Long, DomicilioService> {

    @Autowired
    private DomicilioService domicilioService;

    @Override
    protected DomicilioService getService() {
        return domicilioService;
    }
}
