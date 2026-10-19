package com.biblioteca.servidor.pattern.creational.prototype;

/**
 * Patrón Creacional Prototype:
 * Interfaz que define el contrato para la clonación de objetos del sistema.
 * Permite obtener copias independientes de una entidad sin acoplarse a su clase concreta.
 *
 * @param <T> Tipo de objeto a clonar
 */
public interface Prototype<T> {

    /**
     * Realiza una copia / clonación del objeto prototipo actual.
     * @return Una nueva instancia clonada.
     */
    T clonar();
}

