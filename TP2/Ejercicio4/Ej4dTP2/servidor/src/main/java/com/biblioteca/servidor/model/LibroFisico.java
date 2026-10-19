package com.biblioteca.servidor.model;

import com.biblioteca.servidor.pattern.creational.builder.LibroFisicoBuilder;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.time.LocalDate;
import java.util.List;

/**
 * Entidad LibroFisico: Especialización concreta de Libro para ejemplares en soporte papel / impreso.
 * Incorpora atributos físicos de almacenamiento como número de estantería y ubicación física en biblioteca.
 */
@Entity
@DiscriminatorValue("FISICO")
public class LibroFisico extends Libro {

    @Column(name = "numero_estanteria")
    private String numeroEstanteria;

    @Column(name = "ubicacion")
    private String ubicacion;

    public LibroFisico() {
        super();
        setTipoLibro("FISICO");
    }

    public LibroFisico(Long id, String titulo, int fecha, String genero, int paginas, String autor,
                       LocalDate fechaVencimientoDevolucion, List<Autor> autores,
                       String numeroEstanteria, String ubicacion) {
        super(id, titulo, fecha, genero, paginas, autor, fechaVencimientoDevolucion, autores);
        this.numeroEstanteria = numeroEstanteria;
        this.ubicacion = ubicacion;
        setTipoLibro("FISICO");
    }

    @Override
    public String getTipoLibro() {
        return "FISICO";
    }

    public static LibroFisicoBuilder builder() {
        return new LibroFisicoBuilder();
    }

    public String getNumeroEstanteria() {
        return numeroEstanteria;
    }

    public void setNumeroEstanteria(String numeroEstanteria) {
        this.numeroEstanteria = numeroEstanteria;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}

