package com.biblioteca.servidor.mapper;

import com.biblioteca.servidor.dto.DomicilioDTO;
import com.biblioteca.servidor.model.Domicilio;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LocalidadMapper.class})
public interface DomicilioMapper {

    DomicilioDTO toDTO(Domicilio entidad);

    Domicilio toEntity(DomicilioDTO dto);

    List<DomicilioDTO> toDTOList(List<Domicilio> entidades);

    List<Domicilio> toEntityList(List<DomicilioDTO> dtos);
}

