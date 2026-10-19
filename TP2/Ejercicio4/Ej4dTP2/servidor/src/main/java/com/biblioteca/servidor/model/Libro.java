package com.biblioteca.servidor.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad base abstracta Libro.
 * Representa los atributos y comportamientos comunes de cualquier obra literaria.
 * Soporta herencia polimórfica JPA (SINGLE_TABLE) para los tipos concretos
 * LibroFisico y LibroDigital creados mediante el Patrón Fábrica y Patrón Builder.
 */
@Entity
@Table(name = "libros")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_libro", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "tipoLibro",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = LibroFisico.class, name = "FISICO"),
        @JsonSubTypes.Type(value = LibroDigital.class, name = "DIGITAL")
})
public abstract class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private int fecha; // Año de publicación

    @Column(nullable = false)
    private String genero;

    @Column(nullable = false)
    private int paginas;

    private String autor; // Autor descriptivo textual de cabecera

    @Column
    private LocalDate fechaVencimientoDevolucion; // Fecha límite de préstamo

    @Column(name = "tipo_libro", insertable = false, updatable = false)
    private String tipoLibro;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "libro_autor",
        joinColumns = @JoinColumn(name = "libro_id"),
        inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private List<Autor> autores = new ArrayList<>();

    public Libro() {
    }

    public Libro(Long id, String titulo, int fecha, String genero, int paginas, String autor,
                 LocalDate fechaVencimientoDevolucion, List<Autor> autores) {
        this.id = id;
        this.titulo = titulo;
        this.fecha = fecha;
        this.genero = genero;
        this.paginas = paginas;
        this.autor = autor;
        this.fechaVencimientoDevolucion = fechaVencimientoDevolucion;
        this.autores = autores != null ? new ArrayList<>(autores) : new ArrayList<>();
    }

    public abstract String getTipoLibro();

    public void setTipoLibro(String tipoLibro) {
        this.tipoLibro = tipoLibro;
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
        this.autores = autores != null ? autores : new ArrayList<>();
    }
}
