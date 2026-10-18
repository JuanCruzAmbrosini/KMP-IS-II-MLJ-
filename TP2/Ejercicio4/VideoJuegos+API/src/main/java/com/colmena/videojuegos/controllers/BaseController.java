package com.colmena.videojuegos.controllers;

import com.colmena.videojuegos.services.BaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador base genérico que implementa el patrón Template Method
 * para la capa web REST.
 *
 * Define el esqueleto del algoritmo para el ciclo de vida de peticiones HTTP
 * (GET, GET by ID, POST, PUT, DELETE), delegando la obtención del servicio (getService)
 * a los controladores concretos y proveyendo métodos gancho (hooks) para validaciones,
 * logging y manipulación de datos antes o después de cada operación.
 *
 * @param <E>  Tipo de la entidad
 * @param <ID> Tipo del identificador de la entidad
 * @param <S>  Tipo del servicio base correspondiente
 */
@CrossOrigin(origins = "*")
public abstract class BaseController<E, ID, S extends BaseService<E, ID>> {

    // --- Método primitivo abstracto (obligatorio para cada controlador concreto) ---

    protected abstract S getService();

    // --- Métodos Plantilla (Template Methods) ---

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
        try {
            getService().deleteById(id);
            postDelete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // --- Métodos Gancho (Hooks: opcionales de sobreescribir) ---

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
