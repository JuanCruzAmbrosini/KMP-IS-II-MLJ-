package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Domicilio;
import com.biblioteca.servidor.repository.DomicilioRepository;
import com.biblioteca.servidor.repository.LocalidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class DomicilioService extends BaseService<Domicilio, Long> {

    @Autowired
    private DomicilioRepository domicilioRepository;

    @Autowired
    private LocalidadRepository localidadRepository;

    @Override
    protected JpaRepository<Domicilio, Long> getRepository() {
        return domicilioRepository;
    }

    @Override
    protected String getEntityName() {
        return "Domicilio";
    }

    @Override
    protected void preSave(Domicilio domicilio) {
        if (domicilio.getLocalidad() != null && domicilio.getLocalidad().getId() != null) {
            domicilio.setLocalidad(localidadRepository.findById(domicilio.getLocalidad().getId()).orElse(null));
        }
    }

    @Override
    protected void actualizarDatos(Domicilio existente, Domicilio nuevo) {
        existente.setCalle(nuevo.getCalle());
        existente.setNumero(nuevo.getNumero());
        existente.setLatitud(nuevo.getLatitud());
        existente.setLongitud(nuevo.getLongitud());
        if (nuevo.getLocalidad() != null && nuevo.getLocalidad().getId() != null) {
            existente.setLocalidad(localidadRepository.findById(nuevo.getLocalidad().getId()).orElse(null));
        } else {
            existente.setLocalidad(null);
        }
    }
}
