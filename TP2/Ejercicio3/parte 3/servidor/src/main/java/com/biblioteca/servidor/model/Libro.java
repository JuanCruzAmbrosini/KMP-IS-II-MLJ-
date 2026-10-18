package com.biblioteca.servidor.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private int fecha; // anio de publicacion segun diagrama

    @Column(nullable = false)
    private String genero;

    @Column(nullable = false)
    private int paginas;

    private String autor; // autor descriptivo textual adicional segun diagrama

    @Column
    private LocalDate fechaVencimientoDevolucion; // Fecha limite de devolucion del prestamo

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "libro_autor",
        joinColumns = @JoinColumn(name = "libro_id"),
        inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> autores = new ArrayList<>();

    public Libro() {
    }

    public Libro(Long id, String titulo, int fecha, String genero, int paginas, String autor, List<Autor> autores) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.genero = genero;
        this.paginas = paginas;
        this.autor = autor;
        this.autores = autores != null ? autores : new ArrayList<>();
    }

    public Libro(Long id, String titulo, int fecha, String genero, int paginas, String autor, LocalDate fechaVencimientoDevolucion, List<Autor> autores) {
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

    public List<Autor> getAutores() {
        return autores;
    }

    public void setAutores(List<Autor> autores) {
        this.autores = autores;
    }
}
