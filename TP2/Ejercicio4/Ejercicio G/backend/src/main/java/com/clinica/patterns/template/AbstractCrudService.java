package com.clinica.patterns.template;

import com.clinica.model.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * PATRÓN DE COMPORTAMIENTO: Template Method
 * 
 * Define el esqueleto inmutable de los algoritmos CRUD (guardado, eliminación lógica)
 * delegando en las subclases pasos específicos de validación, preparación y acciones posteriores.
 */
public abstract class AbstractCrudService<T extends BaseEntity, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    // ==========================================
    // TEMPLATE METHOD: Ciclo de Guardado
    // ==========================================
    @Transactional
    public final T guardar(T entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException("La entidad a guardar no puede ser nula.");
        }

        // Paso 1: Validación de reglas de negocio específicas de la entidad
        validarReglasDeNegocio(entidad);

        // Paso 2: Hook previo al guardado (normalización, asignaciones pre-persistencia)
        preGuardado(entidad);

        // Paso 3: Persistencia en base de datos
        T entidadPersistida = getRepository().save(entidad);

        // Paso 4: Hook posterior al guardado (auditoría, eventos, sincronización)
        postGuardado(entidadPersistida);

        return entidadPersistida;
    }

    // ==========================================
    // TEMPLATE METHOD: Ciclo de Eliminación Lógica (Soft Delete)
    // ==========================================
    @Transactional
    public final void eliminar(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID para eliminación no puede ser nulo.");
        }

        // Paso 1: Validación previa a la eliminación
        validarReglasEliminacion(id);

        // Paso 2: Hook previo a eliminación
        preEliminado(id);

        // Paso 3: Ejecución de borrado (gracias a @SQLDelete se ejecuta como UPDATE eliminado = true)
        ejecutarEliminacion(id);

        // Paso 4: Hook posterior
        postEliminado(id);
    }

    // ==========================================
    // Métodos Hook y Abstractos para subclases
    // ==========================================

    /**
     * Paso obligatorio de validación de invariantes y reglas de dominio.
     */
    protected abstract void validarReglasDeNegocio(T entidad);

    /**
     * Hook opcional previo al guardado.
     */
    protected void preGuardado(T entidad) {
        // Implementación por defecto vacía (Hook)
    }

    /**
     * Hook opcional posterior al guardado.
     */
    protected void postGuardado(T entidadPersistida) {
        // Implementación por defecto vacía (Hook)
    }

    /**
     * Hook para validar si una entidad puede ser borrada.
     */
    protected void validarReglasEliminacion(ID id) {
        if (!getRepository().existsById(id)) {
            throw new RuntimeException("No existe el registro con ID: " + id);
        }
    }

    /**
     * Hook previo al borrado lógico.
     */
    protected void preEliminado(ID id) {
        // Hook
    }

    /**
     * Paso de persistencia de eliminación.
     */
    protected void ejecutarEliminacion(ID id) {
        getRepository().deleteById(id);
    }

    /**
     * Hook posterior a eliminación.
     */
    protected void postEliminado(ID id) {
        // Hook
    }

    // ==========================================
    // Métodos de consulta estándar
    // ==========================================
    @Transactional(readOnly = true)
    public List<T> listarTodos() {
        return getRepository().findAll();
    }

    @Transactional(readOnly = true)
    public Optional<T> buscarPorId(ID id) {
        return getRepository().findById(id);
    }
}
