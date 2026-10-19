package com.biblioteca.cliente.dto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibroDTO {
    private Long id;
    private String titulo;
    private int fecha;
    private String genero;
    private int paginas;
    private String autor;
    private LocalDate fechaVencimientoDevolucion;
    private String tipoLibro; // "FISICO" o "DIGITAL"

    // Atributos de LibroFisico
    private String numeroEstanteria;
    private String ubicacion;

    // Atributos de LibroDigital
    private String formato;
    private Double tamanioMB;
    private String urlDescarga;

    private List<AutorDTO> autores = new ArrayList<>();

    public LibroDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getFecha() {
        return fecha;
    }

    public void setFecha(int fecha) {
        this.fecha = fecha;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public LocalDate getFechaVencimientoDevolucion() {
        return fechaVencimientoDevolucion;
    }

    public void setFechaVencimientoDevolucion(LocalDate fechaVencimientoDevolucion) {
        this.fechaVencimientoDevolucion = fechaVencimientoDevolucion;
    }

    public String getTipoLibro() {
        return tipoLibro;
    }

    public void setTipoLibro(String tipoLibro) {
        this.tipoLibro = tipoLibro;
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

    public List<AutorDTO> getAutores() {
        return autores;
    }

    public void setAutores(List<AutorDTO> autores) {
        this.autores = autores != null ? autores : new ArrayList<>();
    }
}
