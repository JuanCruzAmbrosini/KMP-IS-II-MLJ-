package com.tp2.ejercicio1.clima.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO que representa un resultado de geolocalizacion devuelto por la API externa.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CiudadBusquedaDTO {
    private Long id;
    private String nombre;
    private String pais;
    private String region;
    private Double latitud;
    private Double longitud;
    private String codigoPais;

    public CiudadBusquedaDTO() {
    }

    public CiudadBusquedaDTO(Long id, String nombre, String pais, String region, Double latitud, Double longitud, String codigoPais) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
        this.region = region;
        this.latitud = latitud;
        this.longitud = longitud;
        this.codigoPais = codigoPais;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }

    @Override
    public String toString() {
        return nombre + (region != null && !region.isBlank() ? ", " + region : "") + ", " + pais;
    }
}
