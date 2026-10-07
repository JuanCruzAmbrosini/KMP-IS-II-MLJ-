package com.biblioteca.servidor.mapper;

import com.biblioteca.servidor.dto.PersonaDTO;
import com.biblioteca.servidor.model.Persona;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-07T18:31:34-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 26.0.2.1 (Oracle Corporation)"
)
@Component
public class PersonaMapperImpl implements PersonaMapper {

    @Autowired
    private DomicilioMapper domicilioMapper;
    @Autowired
    private LibroMapper libroMapper;

    @Override
    public PersonaDTO toDTO(Persona entidad) {
        if ( entidad == null ) {
            return null;
        }

        PersonaDTO personaDTO = new PersonaDTO();

        personaDTO.setId( entidad.getId() );
        personaDTO.setNombre( entidad.getNombre() );
        personaDTO.setApellido( entidad.getApellido() );
        personaDTO.setDni( entidad.getDni() );
        personaDTO.setDomicilio( domicilioMapper.toDTO( entidad.getDomicilio() ) );
        personaDTO.setLibros( libroMapper.toDTOList( entidad.getLibros() ) );

        return personaDTO;
    }

    @Override
    public Persona toEntity(PersonaDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Persona persona = new Persona();

        persona.setId( dto.getId() );
        persona.setNombre( dto.getNombre() );
        persona.setApellido( dto.getApellido() );
        persona.setDni( dto.getDni() );
        persona.setDomicilio( domicilioMapper.toEntity( dto.getDomicilio() ) );
        persona.setLibros( libroMapper.toEntityList( dto.getLibros() ) );

        return persona;
    }

    @Override
    public List<PersonaDTO> toDTOList(List<Persona> entidades) {
        if ( entidades == null ) {
            return null;
        }

        List<PersonaDTO> list = new ArrayList<PersonaDTO>( entidades.size() );
        for ( Persona persona : entidades ) {
            list.add( toDTO( persona ) );
        }

        return list;
    }

    @Override
    public List<Persona> toEntityList(List<PersonaDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<Persona> list = new ArrayList<Persona>( dtos.size() );
        for ( PersonaDTO personaDTO : dtos ) {
            list.add( toEntity( personaDTO ) );
        }

        return list;
    }
}
