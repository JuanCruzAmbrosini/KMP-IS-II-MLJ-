package com.biblioteca.servidor.pattern.behavioral.iterator;

import com.biblioteca.servidor.model.Libro;

/**
 * Patrón Comportamiento Iterator:
 * Interfaz que define las operaciones estándar para recorrer secuencialmente una colección de libros
 * sin exponer la representación interna subyacente.
 */
public interface LibroIterator {

    /**
     * Verifica si existe otro libro en la colección que cumpla con el criterio de iteración.
     */
    boolean hasNext();

    /**
     * Retorna el siguiente libro del recorrido.
     */
    Libro next();
}

