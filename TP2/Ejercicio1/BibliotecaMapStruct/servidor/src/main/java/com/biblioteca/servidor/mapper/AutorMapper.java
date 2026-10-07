package com.biblioteca.servidor.mapper;

import com.biblioteca.servidor.dto.AutorDTO;
import com.biblioteca.servidor.model.Autor;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AutorMapper {

    AutorDTO toDTO(Autor entidad);

    Autor toEntity(AutorDTO dto);

    List<AutorDTO> toDTOList(List<Autor> entidades);

    List<Autor> toEntityList(List<AutorDTO> dtos);
}

