package com.biblioteca.servidor.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public abstract class BaseService<E, ID> {

    protected abstract JpaRepository<E, ID> getRepository();

    protected abstract void actualizarDatos(E existente, E nuevo);

    protected String getEntityName() {
        return "Entidad";
    }

    public List<E> findAll() {
        preFindAll();
        List<E> entities = getRepository().findAll();
        postFindAll(entities);
        return entities;
    }

    public Optional<E> findById(ID id) {
        preFindById(id);
        Optional<E> entity = getRepository().findById(id);
        postFindById(id, entity);
        return entity;
    }

    public E save(E entity) {
        validar(entity);
        preSave(entity);
        E saved = getRepository().save(entity);
        postSave(saved);
        return saved;
    }

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

    public void deleteById(ID id) {
        preDelete(id);
        getRepository().deleteById(id);
        postDelete(id);
    }

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
