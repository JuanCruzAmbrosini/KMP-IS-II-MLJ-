package com.biblioteca.cliente.dto;

public class DomicilioDTO {
    private Long id;
    private String calle;
    private int numero;
    private String latitud;
    private String longitud;
    private LocalidadDTO localidad;

    public DomicilioDTO() {
    }

    public DomicilioDTO(Long id, String calle, int numero, LocalidadDTO localidad) {
        this(id, calle, numero, null, null, localidad);
    }

    public DomicilioDTO(Long id, String calle, int numero, String latitud, String longitud, LocalidadDTO localidad) {
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

    public LocalidadDTO getLocalidad() {
        return localidad;
    }

    public void setLocalidad(LocalidadDTO localidad) {
        this.localidad = localidad;
    }
}
