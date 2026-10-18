package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.Persona;
import com.biblioteca.servidor.repository.DomicilioRepository;
import com.biblioteca.servidor.repository.LibroRepository;
import com.biblioteca.servidor.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonaService extends BaseService<Persona, Long> {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private DomicilioRepository domicilioRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Override
    protected JpaRepository<Persona, Long> getRepository() {
        return personaRepository;
    }

    @Override
    protected String getEntityName() {
        return "Persona";
    }

    @Override
    protected void preSave(Persona persona) {
        if (persona.getDomicilio() != null && persona.getDomicilio().getId() != null) {
            persona.setDomicilio(domicilioRepository.findById(persona.getDomicilio().getId()).orElse(null));
        }
        if (persona.getLibros() != null) {
            List<Libro> managedLibros = persona.getLibros().stream()
                    .map(lib -> libroRepository.findById(lib.getId()).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .toList();
            persona.setLibros(new ArrayList<>(managedLibros));
        }
    }

    @Override
    protected void actualizarDatos(Persona existente, Persona nuevo) {
        existente.setNombre(nuevo.getNombre());
        existente.setApellido(nuevo.getApellido());
        existente.setDni(nuevo.getDni());
        existente.setEmail(nuevo.getEmail());
        existente.setFechaNacimiento(nuevo.getFechaNacimiento());
        if (nuevo.getDomicilio() != null && nuevo.getDomicilio().getId() != null) {
            existente.setDomicilio(domicilioRepository.findById(nuevo.getDomicilio().getId()).orElse(null));
        } else {
            existente.setDomicilio(null);
        }
        if (nuevo.getLibros() != null) {
            List<Libro> managedLibros = nuevo.getLibros().stream()
                    .map(lib -> libroRepository.findById(lib.getId()).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .toList();
            existente.setLibros(new ArrayList<>(managedLibros));
        } else {
            existente.setLibros(new ArrayList<>());
        }
    }
}
