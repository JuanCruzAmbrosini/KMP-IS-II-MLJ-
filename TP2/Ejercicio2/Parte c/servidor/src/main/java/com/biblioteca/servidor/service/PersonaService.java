package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.Persona;
import com.biblioteca.servidor.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private com.biblioteca.servidor.repository.DomicilioRepository domicilioRepository;

    @Autowired
    private com.biblioteca.servidor.repository.LibroRepository libroRepository;

    public List<Persona> findAll() {
        return personaRepository.findAll();
    }

    public Optional<Persona> findById(Long id) {
        return personaRepository.findById(id);
    }

    public Persona save(Persona persona) {
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
        return personaRepository.save(persona);
    }

    public Persona update(Long id, Persona updated) {
        return personaRepository.findById(id)
                .map(p -> {
                    p.setNombre(updated.getNombre());
                    p.setApellido(updated.getApellido());
                    p.setDni(updated.getDni());
                    p.setEmail(updated.getEmail());
                    p.setFechaNacimiento(updated.getFechaNacimiento());
                    if (updated.getDomicilio() != null && updated.getDomicilio().getId() != null) {
                        p.setDomicilio(domicilioRepository.findById(updated.getDomicilio().getId()).orElse(null));
                    } else {
                        p.setDomicilio(null);
                    }
                    if (updated.getLibros() != null) {
                        List<Libro> managedLibros = updated.getLibros().stream()
                                .map(lib -> libroRepository.findById(lib.getId()).orElse(null))
                                .filter(java.util.Objects::nonNull)
                                .toList();
                        p.setLibros(new ArrayList<>(managedLibros));
                    } else {
                        p.setLibros(new ArrayList<>());
                    }
                    return personaRepository.save(p);
                })
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con id: " + id));
    }

    public void deleteById(Long id) {
        personaRepository.deleteById(id);
    }
}
