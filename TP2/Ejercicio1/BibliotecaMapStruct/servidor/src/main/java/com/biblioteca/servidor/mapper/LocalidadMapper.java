package com.biblioteca.servidor.mapper;

import com.biblioteca.servidor.dto.LocalidadDTO;
import com.biblioteca.servidor.model.Localidad;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LocalidadMapper {

    LocalidadDTO toDTO(Localidad entidad);

    Localidad toEntity(LocalidadDTO dto);

    List<LocalidadDTO> toDTOList(List<Localidad> entidades);

    List<Localidad> toEntityList(List<LocalidadDTO> dtos);
}

