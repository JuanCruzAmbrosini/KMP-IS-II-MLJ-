package com.biblioteca.servidor.pattern.structural.adapter;

import com.biblioteca.servidor.dto.AutorDTO;
import com.biblioteca.servidor.dto.LibroDTO;
import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.LibroDigital;
import com.biblioteca.servidor.model.LibroFisico;
import com.biblioteca.servidor.pattern.creational.factory.LibroFactoryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Patrón Estructural Adapter:
 * Adaptador que convierte entre la jerarquía de entidades Libro (LibroFisico / LibroDigital)
 * y el contrato externo LibroDTO. En la conversión a entidad, integra el Patrón Fábrica (LibroFactoryProvider).
 */
@Component
public class LibroAdapter implements EntityDtoAdapter<Libro, LibroDTO> {

    @Autowired
    private AutorAdapter autorAdapter;

    @Autowired
    private LibroFactoryProvider libroFactoryProvider;

    @Override
    public LibroDTO toDto(Libro entity) {
        if (entity == null) {
            return null;
        }

        LibroDTO dto = new LibroDTO();
        dto.setId(entity.getId());
        dto.setTitulo(entity.getTitulo());
        dto.setFecha(entity.getFecha());
        dto.setGenero(entity.getGenero());
        dto.setPaginas(entity.getPaginas());
        dto.setAutor(entity.getAutor());
        dto.setFechaVencimientoDevolucion(entity.getFechaVencimientoDevolucion());
        dto.setTipoLibro(entity.getTipoLibro());

        if (entity instanceof LibroFisico fisico) {
            dto.setNumeroEstanteria(fisico.getNumeroEstanteria());
            dto.setUbicacion(fisico.getUbicacion());
        } else if (entity instanceof LibroDigital digital) {
            dto.setFormato(digital.getFormato());
            dto.setTamanioMB(digital.getTamanioMB());
            dto.setUrlDescarga(digital.getUrlDescarga());
        }

        if (entity.getAutores() != null) {
            List<AutorDTO> autoresDTO = entity.getAutores().stream()
                    .map(autorAdapter::toDto)
                    .toList();
            dto.setAutores(new ArrayList<>(autoresDTO));
        }

        return dto;
    }

    @Override
    public Libro toEntity(LibroDTO dto) {
        if (dto == null) {
            return null;
        }

        // Se delega la fabricación polimórfica al Patrón Factory
        Libro libro = libroFactoryProvider.crear(dto);

        if (dto.getAutores() != null && !dto.getAutores().isEmpty()) {
            List<Autor> autores = dto.getAutores().stream()
                    .map(autorAdapter::toEntity)
                    .toList();
            libro.setAutores(new ArrayList<>(autores));
        }

        return libro;
    }
}

