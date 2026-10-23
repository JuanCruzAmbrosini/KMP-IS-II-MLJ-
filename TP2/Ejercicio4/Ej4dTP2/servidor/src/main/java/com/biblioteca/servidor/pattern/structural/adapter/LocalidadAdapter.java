package com.biblioteca.servidor.pattern.structural.adapter;

import com.biblioteca.servidor.dto.LocalidadDTO;
import com.biblioteca.servidor.model.Localidad;
import org.springframework.stereotype.Component;

/**
 * Patrón Estructural Adapter:
 * Adaptador para transformar entre la entidad Localidad y LocalidadDTO.
 */
@Component
public class LocalidadAdapter implements EntityDtoAdapter<Localidad, LocalidadDTO> {

    @Override
    public LocalidadDTO toDto(Localidad entity) {
        if (entity == null) {
            return null;
        }
        LocalidadDTO dto = new LocalidadDTO();
        dto.setId(entity.getId());
        dto.setDenominacion(entity.getDenominacion());
        return dto;
    }

    @Override
    public Localidad toEntity(LocalidadDTO dto) {
        if (dto == null) {
            return null;
        }
        Localidad localidad = new Localidad();
        localidad.setId(dto.getId());
        localidad.setDenominacion(dto.getDenominacion());
        return localidad;
    }
}

