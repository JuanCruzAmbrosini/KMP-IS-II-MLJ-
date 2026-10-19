package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Localidad;
import com.biblioteca.servidor.repository.LocalidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class LocalidadService extends BaseService<Localidad, Long> {

    @Autowired
    private LocalidadRepository localidadRepository;

    @Override
    protected JpaRepository<Localidad, Long> getRepository() {
        return localidadRepository;
    }

    @Override
    protected String getEntityName() {
        return "Localidad";
    }

    @Override
    protected void actualizarDatos(Localidad existente, Localidad nuevo) {
        existente.setDenominacion(nuevo.getDenominacion());
    }
}
