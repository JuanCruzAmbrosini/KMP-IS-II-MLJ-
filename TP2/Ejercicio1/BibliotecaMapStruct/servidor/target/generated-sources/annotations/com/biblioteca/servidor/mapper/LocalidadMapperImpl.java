package com.biblioteca.servidor.mapper;

import com.biblioteca.servidor.dto.LocalidadDTO;
import com.biblioteca.servidor.model.Localidad;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-07T18:31:34-0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 26.0.2.1 (Oracle Corporation)"
)
@Component
public class LocalidadMapperImpl implements LocalidadMapper {

    @Override
    public LocalidadDTO toDTO(Localidad entidad) {
        if ( entidad == null ) {
            return null;
        }

        LocalidadDTO localidadDTO = new LocalidadDTO();

        localidadDTO.setId( entidad.getId() );
        localidadDTO.setDenominacion( entidad.getDenominacion() );

        return localidadDTO;
    }

    @Override
    public Localidad toEntity(LocalidadDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Localidad localidad = new Localidad();

        localidad.setId( dto.getId() );
        localidad.setDenominacion( dto.getDenominacion() );

        return localidad;
    }

    @Override
    public List<LocalidadDTO> toDTOList(List<Localidad> entidades) {
        if ( entidades == null ) {
            return null;
        }

        List<LocalidadDTO> list = new ArrayList<LocalidadDTO>( entidades.size() );
        for ( Localidad localidad : entidades ) {
            list.add( toDTO( localidad ) );
        }

        return list;
    }

    @Override
    public List<Localidad> toEntityList(List<LocalidadDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<Localidad> list = new ArrayList<Localidad>( dtos.size() );
        for ( LocalidadDTO localidadDTO : dtos ) {
            list.add( toEntity( localidadDTO ) );
        }

        return list;
    }
}
