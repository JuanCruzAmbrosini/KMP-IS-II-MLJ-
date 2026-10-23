package com.biblioteca.servidor.pattern.structural.adapter;

import com.biblioteca.servidor.dto.DomicilioDTO;
import com.biblioteca.servidor.model.Domicilio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Patrón Estructural Adapter:
 * Adaptador para transformar entre la entidad Domicilio y DomicilioDTO.
 */
@Component
public class DomicilioAdapter implements EntityDtoAdapter<Domicilio, DomicilioDTO> {

    @Autowired
    private LocalidadAdapter localidadAdapter;

    @Override
    public DomicilioDTO toDto(Domicilio entity) {
        if (entity == null) {
            return null;
        }
        DomicilioDTO dto = new DomicilioDTO();
        dto.setId(entity.getId());
        dto.setCalle(entity.getCalle());
        dto.setNumero(entity.getNumero());
        dto.setLatitud(entity.getLatitud());
        dto.setLongitud(entity.getLongitud());
        dto.setLocalidad(localidadAdapter.toDto(entity.getLocalidad()));
        return dto;
    }

    @Override
    public Domicilio toEntity(DomicilioDTO dto) {
        if (dto == null) {
            return null;
        }
        Domicilio domicilio = new Domicilio();
        domicilio.setId(dto.getId());
        domicilio.setCalle(dto.getCalle());
        domicilio.setNumero(dto.getNumero());
        domicilio.setLatitud(dto.getLatitud());
        domicilio.setLongitud(dto.getLongitud());
        domicilio.setLocalidad(localidadAdapter.toEntity(dto.getLocalidad()));
        return domicilio;
    }
}

