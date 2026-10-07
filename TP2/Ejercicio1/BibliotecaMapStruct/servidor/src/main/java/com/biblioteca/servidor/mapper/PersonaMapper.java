package com.biblioteca.servidor.mapper;

import com.biblioteca.servidor.dto.PersonaDTO;
import com.biblioteca.servidor.model.Persona;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DomicilioMapper.class, LibroMapper.class})
public interface PersonaMapper {

    PersonaDTO toDTO(Persona entidad);

    Persona toEntity(PersonaDTO dto);

    List<PersonaDTO> toDTOList(List<Persona> entidades);

    List<Persona> toEntityList(List<PersonaDTO> dtos);
}

