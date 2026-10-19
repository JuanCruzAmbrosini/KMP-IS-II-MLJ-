package com.biblioteca.servidor.pattern.structural.adapter;

/**
 * Patrón Estructural Adapter:
 * Interfaz que define el contrato estándar para adaptar objetos entre
 * la capa de transferencia (DTO) y la capa de dominio/persistencia (Entidad JPA).
 *
 * @param <E> Clase de la Entidad del dominio
 * @param <D> Clase del Data Transfer Object (DTO)
 */
public interface EntityDtoAdapter<E, D> {

    /**
     * Adapta una Entidad del modelo de dominio a su correspondiente DTO.
     */
    D toDto(E entity);

    /**
     * Adapta un DTO a la correspondiente Entidad del modelo de dominio.
     */
    E toEntity(D dto);
}

