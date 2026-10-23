package com.biblioteca.servidor.controller;

import com.biblioteca.servidor.service.BaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

public abstract class BaseController<E, ID, S extends BaseService<E, ID>> {

    protected abstract S getService();

    @GetMapping
    public ResponseEntity<List<E>> getAll() {
        preGetAll();
        List<E> entities = getService().findAll();
        postGetAll(entities);
        return ResponseEntity.ok(entities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<E> getById(@PathVariable ID id) {
        preGetById(id);
        return getService().findById(id)
                .map(entity -> {
                    postGetById(entity);
                    return ResponseEntity.ok(entity);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<E> create(@RequestBody E entity) {
        validarEntidad(entity);
        preCreate(entity);
        E created = getService().save(entity);
        postCreate(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<E> update(@PathVariable ID id, @RequestBody E entity) {
        validarEntidad(entity);
        preUpdate(id, entity);
        try {
            E updated = getService().update(id, entity);
            postUpdate(updated);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable ID id) {
        preDelete(id);
        getService().deleteById(id);
        postDelete(id);
        return ResponseEntity.noContent().build();
    }

    protected void validarEntidad(E entity) {}

    protected void preGetAll() {}

    protected void postGetAll(List<E> entities) {}

    protected void preGetById(ID id) {}

    protected void postGetById(E entity) {}

    protected void preCreate(E entity) {}

    protected void postCreate(E entity) {}

    protected void preUpdate(ID id, E entity) {}

    protected void postUpdate(E entity) {}

    protected void preDelete(ID id) {}

    protected void postDelete(ID id) {}
}
