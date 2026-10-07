package com.biblioteca.servidor.mapper;

import com.biblioteca.servidor.dto.LibroDTO;
import com.biblioteca.servidor.model.Libro;
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
public class LibroMapperImpl implements LibroMapper {

    @Autowired
    private AutorMapper autorMapper;

    @Override
    public LibroDTO toDTO(Libro entidad) {
        if ( entidad == null ) {
            return null;
        }

        LibroDTO libroDTO = new LibroDTO();

        libroDTO.setId( entidad.getId() );
        libroDTO.setTitulo( entidad.getTitulo() );
        libroDTO.setFecha( entidad.getFecha() );
        libroDTO.setGenero( entidad.getGenero() );
        libroDTO.setPaginas( entidad.getPaginas() );
        libroDTO.setAutor( entidad.getAutor() );
        libroDTO.setAutores( autorMapper.toDTOList( entidad.getAutores() ) );

        return libroDTO;
    }

    @Override
    public Libro toEntity(LibroDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Libro libro = new Libro();

        libro.setId( dto.getId() );
        libro.setTitulo( dto.getTitulo() );
        libro.setFecha( dto.getFecha() );
        libro.setGenero( dto.getGenero() );
        libro.setPaginas( dto.getPaginas() );
        libro.setAutor( dto.getAutor() );
        libro.setAutores( autorMapper.toEntityList( dto.getAutores() ) );

        return libro;
    }

    @Override
    public List<LibroDTO> toDTOList(List<Libro> entidades) {
        if ( entidades == null ) {
            return null;
        }

        List<LibroDTO> list = new ArrayList<LibroDTO>( entidades.size() );
        for ( Libro libro : entidades ) {
            list.add( toDTO( libro ) );
        }

        return list;
    }

    @Override
    public List<Libro> toEntityList(List<LibroDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<Libro> list = new ArrayList<Libro>( dtos.size() );
        for ( LibroDTO libroDTO : dtos ) {
            list.add( toEntity( libroDTO ) );
        }

        return list;
    }
}
