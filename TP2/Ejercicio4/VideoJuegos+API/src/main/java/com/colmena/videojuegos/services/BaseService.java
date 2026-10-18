package com.colmena.videojuegos.services;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Clase base abstracta genérica que implementa el patrón Template Method
 * para la capa de servicios.
 *
 * Define el esqueleto del algoritmo para operaciones CRUD estándar (findAll, findById,
 * save, update, deleteById), delegando pasos primitivos (getRepository, actualizarDatos)
 * a las subclases y proveyendo métodos gancho (hooks) para extender el comportamiento.
 *
 * @param <E>  Tipo de la entidad
 * @param <ID> Tipo del identificador de la entidad
 */
public abstract class BaseService<E, ID> {

    // --- Métodos primitivos abstractos (obligatorios para las subclases) ---

    protected abstract JpaRepository<E, ID> getRepository();

    protected abstract void actualizarDatos(E existente, E nuevo);

    // --- Métodos primitivos con implementación por defecto ---

    protected String getEntityName() {
        return "Entidad";
    }

    protected void ejecutarEliminacion(ID id) {
        getRepository().deleteById(id);
    }

    // --- Métodos Plantilla (Template Methods) ---

    @Transactional(readOnly = true)
    public List<E> findAll() {
        preFindAll();
        List<E> entities = getRepository().findAll();
        postFindAll(entities);
        return entities;
    }

    @Transactional(readOnly = true)
    public Optional<E> findById(ID id) {
        preFindById(id);
        Optional<E> entity = getRepository().findById(id);
        postFindById(id, entity);
        return entity;
    }

    @Transactional
    public E save(E entity) {
        validar(entity);
        preSave(entity);
        E saved = getRepository().save(entity);
        postSave(saved);
        return saved;
    }

    @Transactional
    public E update(ID id, E entity) {
        validar(entity);
        preUpdate(id, entity);
        E existente = getRepository().findById(id)
                .orElseThrow(() -> new RuntimeException(getEntityName() + " no encontrada con id: " + id));
        actualizarDatos(existente, entity);
        E updated = getRepository().save(existente);
        postUpdate(updated);
        return updated;
    }

    @Transactional
    public void deleteById(ID id) {
        preDelete(id);
        ejecutarEliminacion(id);
        postDelete(id);
    }

    // --- Métodos de compatibilidad con versiones previas ---

    @Transactional
    public E saveOne(E entity) {
        return save(entity);
    }

    @Transactional
    public E updateOne(E entity, ID id) {
        return update(id, entity);
    }

    // --- Métodos Gancho (Hooks: opcionales de sobreescribir) ---

    protected void validar(E entity) {}

    protected void preSave(E entity) {}

    protected void postSave(E entity) {}

    protected void preUpdate(ID id, E entity) {}

    protected void postUpdate(E entity) {}

    protected void preDelete(ID id) {}

    protected void postDelete(ID id) {}

    protected void preFindAll() {}

    protected void postFindAll(List<E> entities) {}

    protected void preFindById(ID id) {}

    protected void postFindById(ID id, Optional<E> entity) {}
}
