package com.biblioteca.servidor.pattern.creational.factory;

import com.biblioteca.servidor.dto.LibroDTO;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.LibroDigital;
import org.springframework.stereotype.Component;

/**
 * Patrón Creacional Fábrica Concreta:
 * Fábrica especializada en la creación de instancias de LibroDigital.
 * Implementa la construcción utilizando LibroDigital.builder().
 */
@Component
public class LibroDigitalFactory implements LibroFactory {

    @Override
    public String getTipoSoportado() {
        return "DIGITAL";
    }

    @Override
    public Libro crearLibro(LibroDTO dto) {
        if (dto == null) {
            return null;
        }

        return LibroDigital.builder()
                .conId(dto.getId())
                .conTitulo(dto.getTitulo())
                .conFecha(dto.getFecha())
                .conGenero(dto.getGenero())
                .conPaginas(dto.getPaginas())
                .conAutor(dto.getAutor())
                .conFechaVencimientoDevolucion(dto.getFechaVencimientoDevolucion())
                .conFormato(dto.getFormato() != null ? dto.getFormato().toUpperCase() : "PDF")
                .conTamanioMB(dto.getTamanioMB() != null ? dto.getTamanioMB() : 5.0)
                .conUrlDescarga(dto.getUrlDescarga() != null ? dto.getUrlDescarga() : "https://biblioteca.frm.utn.edu.ar/repositorio/default.pdf")
                .build();
    }
}

