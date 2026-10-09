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
    private List<AutorDTO> autores = new ArrayList<>();

    public LibroDTO() {
    }

    public LibroDTO(Long id, String titulo, int fecha, String genero, int paginas, String autor, List<AutorDTO> autores) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.genero = genero;
        this.paginas = paginas;
        this.autor = autor;
        this.autores = autores != null ? autores : new ArrayList<>();
    }

    public LibroDTO(Long id, String titulo, int fecha, String genero, int paginas, String autor, LocalDate fechaVencimientoDevolucion, List<AutorDTO> autores) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.genero = genero;
        this.paginas = paginas;
        this.autor = autor;
        this.fechaVencimientoDevolucion = fechaVencimientoDevolucion;
        this.autores = autores != null ? autores : new ArrayList<>();
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

    public List<AutorDTO> getAutores() {
        return autores;
    }

    public void setAutores(List<AutorDTO> autores) {
        this.autores = autores;
    }
}
