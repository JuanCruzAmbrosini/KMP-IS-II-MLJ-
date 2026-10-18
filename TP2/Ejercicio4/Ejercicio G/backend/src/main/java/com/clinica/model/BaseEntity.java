package com.clinica.model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Column;
import java.io.Serializable;
import java.util.UUID;

/**
 * Superclase mapeada que centraliza los requerimientos arquitectónicos globales:
 * 1. Clave primaria id de tipo String (UUID).
 * 2. Borrado lógico mediante el atributo 'eliminado' (soft delete).
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", updatable = false, nullable = false, length = 36)
    private String id;

    @Column(name = "eliminado", nullable = false)
    private boolean eliminado = false;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.trim().isEmpty()) {
            this.id = UUID.randomUUID().toString();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEliminado() {
        return eliminado;
    }

    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }
}
