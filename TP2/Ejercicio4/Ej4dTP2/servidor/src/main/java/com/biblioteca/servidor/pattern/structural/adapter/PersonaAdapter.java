package com.biblioteca.servidor.pattern.structural.adapter;

import com.biblioteca.servidor.dto.LibroDTO;
import com.biblioteca.servidor.dto.PersonaDTO;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Patrón Estructural Adapter:
 * Adaptador para transformar entre la entidad Persona y PersonaDTO.
 */
@Component
public class PersonaAdapter implements EntityDtoAdapter<Persona, PersonaDTO> {

    @Autowired
    private DomicilioAdapter domicilioAdapter;

    @Autowired
    private LibroAdapter libroAdapter;

    @Override
    public PersonaDTO toDto(Persona entity) {
        if (entity == null) {
            return null;
        }
        PersonaDTO dto = new PersonaDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setDni(entity.getDni());
        dto.setEmail(entity.getEmail());
        dto.setFechaNacimiento(entity.getFechaNacimiento());
        dto.setDomicilio(domicilioAdapter.toDto(entity.getDomicilio()));

        if (entity.getLibros() != null) {
            List<LibroDTO> librosDto = entity.getLibros().stream()
                    .map(libroAdapter::toDto)
                    .toList();
            dto.setLibros(new ArrayList<>(librosDto));
        }

        return dto;
    }

    @Override
    public Persona toEntity(PersonaDTO dto) {
        if (dto == null) {
            return null;
        }
        Persona persona = new Persona();
        persona.setId(dto.getId());
        persona.setNombre(dto.getNombre());
        persona.setApellido(dto.getApellido());
        persona.setDni(dto.getDni());
        persona.setEmail(dto.getEmail());
        persona.setFechaNacimiento(dto.getFechaNacimiento());
        persona.setDomicilio(domicilioAdapter.toEntity(dto.getDomicilio()));

        if (dto.getLibros() != null) {
            List<Libro> libros = dto.getLibros().stream()
                    .map(libroAdapter::toEntity)
                    .toList();
            persona.setLibros(new ArrayList<>(libros));
        }

        return persona;
    }
}

