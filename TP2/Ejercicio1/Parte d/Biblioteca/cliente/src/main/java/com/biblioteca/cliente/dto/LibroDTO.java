package com.biblioteca.cliente.dto;

import java.util.ArrayList;
import java.util.List;

public class LibroDTO {
    private Long id;
    private String titulo;
    private int fecha;
    private String genero;
    private int paginas;
    private String autor;
    private boolean tienePdf; // lo informa el servidor: indica si el libro tiene PDF cargado
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

    public boolean isTienePdf() {
        return tienePdf;
    }

    public void setTienePdf(boolean tienePdf) {
        this.tienePdf = tienePdf;
    }

    public List<AutorDTO> getAutores() {
        return autores;
    }

    public void setAutores(List<AutorDTO> autores) {
        this.autores = autores;
    }
}



