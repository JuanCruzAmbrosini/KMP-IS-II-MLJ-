package com.biblioteca.servidor.pattern.creational.builder;

import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.model.Libro;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Patrón Creacional Builder:
 * Clase abstracta genérica que encapsula la construcción paso a paso de objetos de la jerarquía Libro.
 * Emplea Curiously Recurring Template Pattern (CRTP) para habilitar encadenamiento fluido (fluent interface)
 * en subclases sin perder el tipo concreto del Builder.
 *
 * @param <T> Tipo concreto de Libro producido
 * @param <B> Tipo concreto del Builder (para retorno fluido 'this')
 */
public abstract class LibroBuilder<T extends Libro, B extends LibroBuilder<T, B>> {

    protected Long id;
    protected String titulo;
    protected int fecha;
    protected String genero;
    protected int paginas;
    protected String autor;
    protected LocalDate fechaVencimientoDevolucion;
    protected List<Autor> autores = new ArrayList<>();

    @SuppressWarnings("unchecked")
    protected B self() {
        return (B) this;
    }

    public B conId(Long id) {
        this.id = id;
        return self();
    }

    public B conTitulo(String titulo) {
        this.titulo = titulo;
        return self();
    }

    public B conFecha(int fecha) {
        this.fecha = fecha;
        return self();
    }

    public B conGenero(String genero) {
        this.genero = genero;
        return self();
    }

    public B conPaginas(int paginas) {
        this.paginas = paginas;
        return self();
    }

    public B conAutor(String autor) {
        this.autor = autor;
        return self();
    }

    public B conFechaVencimientoDevolucion(LocalDate fechaVencimientoDevolucion) {
        this.fechaVencimientoDevolucion = fechaVencimientoDevolucion;
        return self();
    }

    public B conAutores(List<Autor> autores) {
        this.autores = autores != null ? new ArrayList<>(autores) : new ArrayList<>();
        return self();
    }

    public B agregarAutor(Autor autor) {
        if (autor != null) {
            this.autores.add(autor);
        }
        return self();
    }

    /**
     * Construye y retorna la instancia final del tipo Libro configurado.
     */
    public abstract T build();
}

