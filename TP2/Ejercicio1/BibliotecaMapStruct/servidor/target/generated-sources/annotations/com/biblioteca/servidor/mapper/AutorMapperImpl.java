package com.biblioteca.servidor.mapper;

import com.biblioteca.servidor.dto.AutorDTO;
import com.biblioteca.servidor.model.Autor;
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
public class AutorMapperImpl implements AutorMapper {

    @Override
    public AutorDTO toDTO(Autor entidad) {
        if ( entidad == null ) {
            return null;
        }

        AutorDTO autorDTO = new AutorDTO();

        autorDTO.setId( entidad.getId() );
        autorDTO.setNombre( entidad.getNombre() );
        autorDTO.setApellido( entidad.getApellido() );
        autorDTO.setBiografia( entidad.getBiografia() );

        return autorDTO;
    }

    @Override
    public Autor toEntity(AutorDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Autor autor = new Autor();

        autor.setId( dto.getId() );
        autor.setNombre( dto.getNombre() );
        autor.setApellido( dto.getApellido() );
        autor.setBiografia( dto.getBiografia() );

        return autor;
    }

    @Override
    public List<AutorDTO> toDTOList(List<Autor> entidades) {
        if ( entidades == null ) {
            return null;
        }

        List<AutorDTO> list = new ArrayList<AutorDTO>( entidades.size() );
        for ( Autor autor : entidades ) {
            list.add( toDTO( autor ) );
        }

        return list;
    }

    @Override
    public List<Autor> toEntityList(List<AutorDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<Autor> list = new ArrayList<Autor>( dtos.size() );
        for ( AutorDTO autorDTO : dtos ) {
            list.add( toEntity( autorDTO ) );
        }

        return list;
    }
}
