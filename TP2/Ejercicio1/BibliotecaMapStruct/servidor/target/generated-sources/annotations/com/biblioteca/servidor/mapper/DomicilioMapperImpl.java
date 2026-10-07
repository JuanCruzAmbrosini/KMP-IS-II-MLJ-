package com.biblioteca.servidor.mapper;

import com.biblioteca.servidor.dto.DomicilioDTO;
import com.biblioteca.servidor.model.Domicilio;
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
public class DomicilioMapperImpl implements DomicilioMapper {

    @Autowired
    private LocalidadMapper localidadMapper;

    @Override
    public DomicilioDTO toDTO(Domicilio entidad) {
        if ( entidad == null ) {
            return null;
        }

        DomicilioDTO domicilioDTO = new DomicilioDTO();

        domicilioDTO.setId( entidad.getId() );
        domicilioDTO.setCalle( entidad.getCalle() );
        domicilioDTO.setNumero( entidad.getNumero() );
        domicilioDTO.setLocalidad( localidadMapper.toDTO( entidad.getLocalidad() ) );

        return domicilioDTO;
    }

    @Override
    public Domicilio toEntity(DomicilioDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Domicilio domicilio = new Domicilio();

        domicilio.setId( dto.getId() );
        domicilio.setCalle( dto.getCalle() );
        domicilio.setNumero( dto.getNumero() );
        domicilio.setLocalidad( localidadMapper.toEntity( dto.getLocalidad() ) );

        return domicilio;
    }

    @Override
    public List<DomicilioDTO> toDTOList(List<Domicilio> entidades) {
        if ( entidades == null ) {
            return null;
        }

        List<DomicilioDTO> list = new ArrayList<DomicilioDTO>( entidades.size() );
        for ( Domicilio domicilio : entidades ) {
            list.add( toDTO( domicilio ) );
        }

        return list;
    }

    @Override
    public List<Domicilio> toEntityList(List<DomicilioDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<Domicilio> list = new ArrayList<Domicilio>( dtos.size() );
        for ( DomicilioDTO domicilioDTO : dtos ) {
            list.add( toEntity( domicilioDTO ) );
        }

        return list;
    }
}
