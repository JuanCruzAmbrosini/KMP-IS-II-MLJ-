package com.biblioteca.servidor.pattern.creational.builder;

import com.biblioteca.servidor.model.LibroDigital;

/**
 * Patrón Creacional Builder:
 * Builder concreto especializado en la construcción fluida de instancias de LibroDigital.
 */
public class LibroDigitalBuilder extends LibroBuilder<LibroDigital, LibroDigitalBuilder> {

    private String formato;
    private Double tamanioMB;
    private String urlDescarga;

    public LibroDigitalBuilder conFormato(String formato) {
        this.formato = formato;
        return this;
    }

    public LibroDigitalBuilder conTamanioMB(Double tamanioMB) {
        this.tamanioMB = tamanioMB;
        return this;
    }

    public LibroDigitalBuilder conUrlDescarga(String urlDescarga) {
        this.urlDescarga = urlDescarga;
        return this;
    }

    @Override
    public LibroDigital build() {
        return new LibroDigital(
                this.id,
                this.titulo,
                this.fecha,
                this.genero,
                this.paginas,
                this.autor,
                this.fechaVencimientoDevolucion,
                this.autores,
                this.formato,
                this.tamanioMB,
                this.urlDescarga
        );
    }
}

