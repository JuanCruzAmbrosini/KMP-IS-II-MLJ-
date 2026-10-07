package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Localidad;
import com.biblioteca.servidor.repository.LocalidadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocalidadService {

    @Autowired
    private LocalidadRepository localidadRepository;

    public List<Localidad> findAll() {
        return localidadRepository.findAll();
    }

    public Optional<Localidad> findById(Long id) {
        return localidadRepository.findById(id);
    }

    public Localidad save(Localidad localidad) {
        return localidadRepository.save(localidad);
    }

    public Localidad update(Long id, Localidad updated) {
        return localidadRepository.findById(id)
                .map(loc -> {
                    loc.setDenominacion(updated.getDenominacion());
                    return localidadRepository.save(loc);
                })
                .orElseThrow(() -> new RuntimeException("Localidad no encontrada con id: " + id));
    }

    public void deleteById(Long id) {
        localidadRepository.deleteById(id);
    }
}

