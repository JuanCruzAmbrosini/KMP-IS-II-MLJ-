package com.tp2.ejercicio1.clima.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO que representa la prediccion meteorologica de un dia especifico.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PronosticoDiaDTO {
    private String fecha;
    private String diaSemana;
    private Double tempMin;
    private Double tempMax;
    private Integer probPrecipitacion;
    private Integer codigoClima;
    private String condicionDescripcion;
    private String condicionIcono;

    public PronosticoDiaDTO() {
    }

    public PronosticoDiaDTO(String fecha, String diaSemana, Double tempMin, Double tempMax, Integer probPrecipitacion, Integer codigoClima, String condicionDescripcion, String condicionIcono) {
        this.fecha = fecha;
        this.diaSemana = diaSemana;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.probPrecipitacion = probPrecipitacion;
        this.codigoClima = codigoClima;
        this.condicionDescripcion = condicionDescripcion;
        this.condicionIcono = condicionIcono;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public Double getTempMin() {
        return tempMin;
    }

    public void setTempMin(Double tempMin) {
        this.tempMin = tempMin;
    }

    public Double getTempMax() {
        return tempMax;
    }

    public void setTempMax(Double tempMax) {
        this.tempMax = tempMax;
    }

    public Integer getProbPrecipitacion() {
        return probPrecipitacion;
    }

    public void setProbPrecipitacion(Integer probPrecipitacion) {
        this.probPrecipitacion = probPrecipitacion;
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
}
