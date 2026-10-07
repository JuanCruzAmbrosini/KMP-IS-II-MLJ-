package com.biblioteca.servidor.service;

import com.biblioteca.servidor.dto.PersonaDTO;
import com.biblioteca.servidor.mapper.PersonaMapper;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.Persona;
import com.biblioteca.servidor.repository.DomicilioRepository;
import com.biblioteca.servidor.repository.LibroRepository;
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
    private DomicilioRepository domicilioRepository;

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private PersonaMapper personaMapper;

    public List<PersonaDTO> findAll() {
        List<Persona> entidades = personaRepository.findAll();
        return personaMapper.toDTOList(entidades);
    }

    public Optional<PersonaDTO> findById(Long id) {
        return personaRepository.findById(id)
                .map(personaMapper::toDTO);
    }

    public PersonaDTO save(PersonaDTO dto) {
        Persona entidad = personaMapper.toEntity(dto);
        entidad.setId(null);

        if (dto.getDomicilio() != null && dto.getDomicilio().getId() != null) {
            entidad.setDomicilio(domicilioRepository.findById(dto.getDomicilio().getId()).orElse(null));
        } else {
            entidad.setDomicilio(null);
        }

        if (dto.getLibros() != null) {
            List<Libro> managedLibros = dto.getLibros().stream()
                    .filter(lib -> lib != null && lib.getId() != null)
                    .map(lib -> libroRepository.findById(lib.getId()).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .toList();
            entidad.setLibros(new ArrayList<>(managedLibros));
        } else {
            entidad.setLibros(new ArrayList<>());
        }

        Persona guardada = personaRepository.save(entidad);
        return personaMapper.toDTO(guardada);
    }

    public PersonaDTO update(Long id, PersonaDTO updatedDto) {
        return personaRepository.findById(id)
                .map(p -> {
                    p.setNombre(updatedDto.getNombre());
                    p.setApellido(updatedDto.getApellido());
                    p.setDni(updatedDto.getDni());

                    if (updatedDto.getDomicilio() != null && updatedDto.getDomicilio().getId() != null) {
                        p.setDomicilio(domicilioRepository.findById(updatedDto.getDomicilio().getId()).orElse(null));
                    } else {
                        p.setDomicilio(null);
                    }

                    if (updatedDto.getLibros() != null) {
                        List<Libro> managedLibros = updatedDto.getLibros().stream()
                                .filter(lib -> lib != null && lib.getId() != null)
                                .map(lib -> libroRepository.findById(lib.getId()).orElse(null))
                                .filter(java.util.Objects::nonNull)
                                .toList();
                        p.setLibros(new ArrayList<>(managedLibros));
                    } else {
                        p.setLibros(new ArrayList<>());
                    }

                    Persona guardada = personaRepository.save(p);
                    return personaMapper.toDTO(guardada);
                })
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con id: " + id));
    }

    public void deleteById(Long id) {
        personaRepository.deleteById(id);
    }
}
