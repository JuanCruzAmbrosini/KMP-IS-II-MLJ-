package com.biblioteca.cliente.dto;

public class DomicilioDTO {
    private Long id;
    private String calle;
    private int numero;
    private LocalidadDTO localidad;

    public DomicilioDTO() {
    }

    public DomicilioDTO(Long id, String calle, int numero, LocalidadDTO localidad) {
        this.id = id;
        this.calle = calle;
        this.numero = numero;
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

    public LocalidadDTO getLocalidad() {
        return localidad;
    }

    public void setLocalidad(LocalidadDTO localidad) {
        this.localidad = localidad;
    }
}

