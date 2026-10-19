package com.biblioteca.servidor.model;

import com.biblioteca.servidor.pattern.creational.builder.LibroDigitalBuilder;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.util.List;

/**
 * Entidad LibroDigital: Especialización concreta de Libro para libros electrónicos (e-books).
 * Incorpora atributos digitales tales como formato (PDF, EPUB, etc.), tamaño en Megabytes y URL de descarga.
 */
@Entity
@DiscriminatorValue("DIGITAL")
public class LibroDigital extends Libro {

    @Column(name = "formato")
    private String formato;

    @Column(name = "tamanio_mb")
    private Double tamanioMB;

    @Column(name = "url_descarga")
    private String urlDescarga;

    public LibroDigital() {
        super();
        setTipoLibro("DIGITAL");
    }

    public LibroDigital(Long id, String titulo, int fecha, String genero, int paginas, String autor,
                        LocalDate fechaVencimientoDevolucion, List<Autor> autores,
                        String formato, Double tamanioMB, String urlDescarga) {
        super(id, titulo, fecha, genero, paginas, autor, fechaVencimientoDevolucion, autores);
        this.formato = formato;
        this.tamanioMB = tamanioMB;
        this.urlDescarga = urlDescarga;
        setTipoLibro("DIGITAL");
    }

    @Override
    public String getTipoLibro() {
        return "DIGITAL";
    }

    public static LibroDigitalBuilder builder() {
        return new LibroDigitalBuilder();
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public Double getTamanioMB() {
        return tamanioMB;
    }

    public void setTamanioMB(Double tamanioMB) {
        this.tamanioMB = tamanioMB;
    }

    public String getUrlDescarga() {
        return urlDescarga;
    }

    public void setUrlDescarga(String urlDescarga) {
        this.urlDescarga = urlDescarga;
    }
}

