package com.tp2.ejercicio1.clima.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO que unifica y encapsula la informacion meteorologica actual obtenida
 * desde la API externa, lista para ser consumida y mostrada por el FrontEnd.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClimaActualDTO {
    private String ciudad;
    private String pais;
    private String region;
    private Double latitud;
    private Double longitud;
    private Double temperatura;
    private Double sensacionTermica;
    private Integer humedad;
    private Double presion;
    private Double velocidadViento;
    private Integer direccionViento;
    private Integer codigoClima;
    private String condicionDescripcion;
    private String condicionIcono;
    private String fechaHora;
    private String fuente;

    public ClimaActualDTO() {
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
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

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Double getSensacionTermica() {
        return sensacionTermica;
    }

    public void setSensacionTermica(Double sensacionTermica) {
        this.sensacionTermica = sensacionTermica;
    }

    public Integer getHumedad() {
        return humedad;
    }

    public void setHumedad(Integer humedad) {
        this.humedad = humedad;
    }

    public Double getPresion() {
        return presion;
    }

    public void setPresion(Double presion) {
        this.presion = presion;
    }

    public Double getVelocidadViento() {
        return velocidadViento;
    }

    public void setVelocidadViento(Double velocidadViento) {
        this.velocidadViento = velocidadViento;
    }

    public Integer getDireccionViento() {
        return direccionViento;
    }

    public void setDireccionViento(Integer direccionViento) {
        this.direccionViento = direccionViento;
    }

    public Integer getCodigoClima() {
        return codigoClima;
    }

    public void setCodigoClima(Integer codigoClima) {
        this.codigoClima = codigoClima;
    }

    public String getCondicionDescripcion() {
        return condicionDescripcion;
    }

    public void setCondicionDescripcion(String condicionDescripcion) {
        this.condicionDescripcion = condicionDescripcion;
    }

    public String getCondicionIcono() {
        return condicionIcono;
    }

    public void setCondicionIcono(String condicionIcono) {
        this.condicionIcono = condicionIcono;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getFuente() {
        return fuente;
    }

    public void setFuente(String fuente) {
        this.fuente = fuente;
    }
}
