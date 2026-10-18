package com.biblioteca.servidor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "localidades")
public class Localidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String denominacion;

    public Localidad() {
    }

    public Localidad(Long id, String denominacion) {
        this.id = id;
        this.denominacion = denominacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }
}

