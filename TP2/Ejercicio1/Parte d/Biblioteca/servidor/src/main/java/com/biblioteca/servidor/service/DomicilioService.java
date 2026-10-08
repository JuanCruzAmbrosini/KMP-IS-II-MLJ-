package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Domicilio;
import com.biblioteca.servidor.repository.DomicilioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DomicilioService {

    @Autowired
    private DomicilioRepository domicilioRepository;

    @Autowired
    private com.biblioteca.servidor.repository.LocalidadRepository localidadRepository;

    public List<Domicilio> findAll() {
        return domicilioRepository.findAll();
    }

    public Optional<Domicilio> findById(Long id) {
        return domicilioRepository.findById(id);
    }

    public Domicilio save(Domicilio domicilio) {
        if (domicilio.getLocalidad() != null && domicilio.getLocalidad().getId() != null) {
            domicilio.setLocalidad(localidadRepository.findById(domicilio.getLocalidad().getId()).orElse(null));
        }
        return domicilioRepository.save(domicilio);
    }

    public Domicilio update(Long id, Domicilio updated) {
        return domicilioRepository.findById(id)
                .map(dom -> {
                    dom.setCalle(updated.getCalle());
                    dom.setNumero(updated.getNumero());
                    if (updated.getLocalidad() != null && updated.getLocalidad().getId() != null) {
                        dom.setLocalidad(localidadRepository.findById(updated.getLocalidad().getId()).orElse(null));
                    } else {
                        dom.setLocalidad(null);
                    }
                    return domicilioRepository.save(dom);
                })
                .orElseThrow(() -> new RuntimeException("Domicilio no encontrado con id: " + id));
    }

    public void deleteById(Long id) {
        domicilioRepository.deleteById(id);
    }
}
