package com.biblioteca.servidor.pattern.creational.builder;

import com.biblioteca.servidor.model.LibroFisico;

/**
 * Patrón Creacional Builder:
 * Builder concreto especializado en la construcción fluida de instancias de LibroFisico.
 */
public class LibroFisicoBuilder extends LibroBuilder<LibroFisico, LibroFisicoBuilder> {

    private String numeroEstanteria;
    private String ubicacion;

    public LibroFisicoBuilder conNumeroEstanteria(String numeroEstanteria) {
        this.numeroEstanteria = numeroEstanteria;
        return this;
    }

    public LibroFisicoBuilder conUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
        return this;
    }

    @Override
    public LibroFisico build() {
        return new LibroFisico(
                this.id,
                this.titulo,
                this.fecha,
                this.genero,
                this.paginas,
                this.autor,
                this.fechaVencimientoDevolucion,
                this.autores,
                this.numeroEstanteria,
                this.ubicacion
        );
    }
}

