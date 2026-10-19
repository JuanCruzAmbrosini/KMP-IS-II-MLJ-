package com.biblioteca.servidor.pattern.behavioral.iterator;

import com.biblioteca.servidor.model.Libro;

import java.util.ArrayList;
import java.util.List;

/**
 * Patrón Comportamiento Iterator:
 * Implementación concreta del Aggregate ColeccionLibros.
 */
public class ColeccionLibrosImpl implements ColeccionLibros {

    private final List<Libro> libros;

    public ColeccionLibrosImpl() {
        this.libros = new ArrayList<>();
    }

    public ColeccionLibrosImpl(List<Libro> libros) {
        this.libros = libros != null ? new ArrayList<>(libros) : new ArrayList<>();
    }

    @Override
    public void agregar(Libro libro) {
        if (libro != null) {
            this.libros.add(libro);
        }
    }

    @Override
    public List<Libro> getLibros() {
        return new ArrayList<>(libros);
    }

    @Override
    public LibroIterator crearIteradorPorAutor(String nombreAutor) {
        return new LibroPorAutorIterator(this.libros, nombreAutor);
    }

    @Override
    public LibroIterator crearIteradorPorAutorId(Long autorId) {
        return new LibroPorAutorIterator(this.libros, autorId);
    }
}

