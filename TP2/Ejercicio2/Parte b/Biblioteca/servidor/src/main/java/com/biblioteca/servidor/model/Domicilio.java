package com.biblioteca.servidor.model;

import jakarta.persistence.*;

@Entity
@Table(name = "domicilios")
public class Domicilio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String calle;

    @Column(nullable = false)
    private int numero;

    @Column
    private String latitud;

    @Column
    private String longitud;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "localidad_id")
    private Localidad localidad;

    public Domicilio() {
    }

    public Domicilio(Long id, String calle, int numero, Localidad localidad) {
        this.id = id;
        this.calle = calle;
        this.numero = numero;
        this.localidad = localidad;
    }

    public Domicilio(Long id, String calle, int numero, String latitud, String longitud, Localidad localidad) {
        this.id = id;
        this.calle = calle;
        this.numero = numero;
        this.latitud = latitud;
        this.longitud = longitud;
        this.localidad = localidad;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public Localidad getLocalidad() {
        return localidad;
    }

    public void setLocalidad(Localidad localidad) {
        this.localidad = localidad;
    }
}
