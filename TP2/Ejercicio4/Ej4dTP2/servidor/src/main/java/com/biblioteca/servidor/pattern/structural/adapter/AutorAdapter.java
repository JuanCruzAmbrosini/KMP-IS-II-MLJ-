package com.biblioteca.servidor.pattern.structural.adapter;

import com.biblioteca.servidor.dto.AutorDTO;
import com.biblioteca.servidor.model.Autor;
import org.springframework.stereotype.Component;

/**
 * Patrón Estructural Adapter:
 * Adaptador concreto para transformar entre la entidad Autor y AutorDTO.
 */
@Component
public class AutorAdapter implements EntityDtoAdapter<Autor, AutorDTO> {

    @Override
    public AutorDTO toDto(Autor entity) {
        if (entity == null) {
            return null;
        }
        AutorDTO dto = new AutorDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setApellido(entity.getApellido());
        dto.setBiografia(entity.getBiografia());
        return dto;
    }

    @Override
    public Autor toEntity(AutorDTO dto) {
        if (dto == null) {
            return null;
        }
        Autor autor = new Autor();
        autor.setId(dto.getId());
        autor.setNombre(dto.getNombre());
        autor.setApellido(dto.getApellido());
        autor.setBiografia(dto.getBiografia());
        return autor;
    }
}

