package com.biblioteca.servidor.pattern.creational.factory;

import com.biblioteca.servidor.dto.LibroDTO;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.LibroFisico;
import org.springframework.stereotype.Component;

/**
 * Patrón Creacional Fábrica Concreta:
 * Fábrica especializada en la creación de instancias de LibroFisico.
 * Implementa la construcción utilizando LibroFisico.builder().
 */
@Component
public class LibroFisicoFactory implements LibroFactory {

    @Override
    public String getTipoSoportado() {
        return "FISICO";
    }

    @Override
    public Libro crearLibro(LibroDTO dto) {
        if (dto == null) {
            return null;
        }

        return LibroFisico.builder()
                .conId(dto.getId())
                .conTitulo(dto.getTitulo())
                .conFecha(dto.getFecha())
                .conGenero(dto.getGenero())
                .conPaginas(dto.getPaginas())
                .conAutor(dto.getAutor())
                .conFechaVencimientoDevolucion(dto.getFechaVencimientoDevolucion())
                .conNumeroEstanteria(dto.getNumeroEstanteria() != null ? dto.getNumeroEstanteria() : "Estante General")
                .conUbicacion(dto.getUbicacion() != null ? dto.getUbicacion() : "Biblioteca Central")
                .build();
    }
}

