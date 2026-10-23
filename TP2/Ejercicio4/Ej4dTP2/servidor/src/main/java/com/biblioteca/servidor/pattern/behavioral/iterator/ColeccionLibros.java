package com.biblioteca.servidor.pattern.behavioral.iterator;

import com.biblioteca.servidor.model.Libro;

import java.util.List;

/**
 * Patrón Comportamiento Iterator:
 * Interfaz Aggregate que define el contrato para colecciones que pueden ser recorridas por un LibroIterator.
 */
public interface ColeccionLibros {

    void agregar(Libro libro);

    List<Libro> getLibros();

    /**
     * Crea un iterador especializado para recorrer libros de un autor por su nombre o apellido.
     */
    LibroIterator crearIteradorPorAutor(String nombreAutor);

    /**
     * Crea un iterador especializado para recorrer libros de un autor por su ID.
     */
    LibroIterator crearIteradorPorAutorId(Long autorId);
}

