package com.biblioteca.servidor.mapper;

import com.biblioteca.servidor.dto.LibroDTO;
import com.biblioteca.servidor.model.Libro;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AutorMapper.class})
public interface LibroMapper {

    LibroDTO toDTO(Libro entidad);

    Libro toEntity(LibroDTO dto);

    List<LibroDTO> toDTOList(List<Libro> entidades);

    List<Libro> toEntityList(List<LibroDTO> dtos);
}

