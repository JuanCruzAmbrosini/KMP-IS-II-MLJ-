package com.tp2.ejercicio1.clima.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO que agrupa el clima actual junto con el pronostico extendido para los proximos dias.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PronosticoCompletoDTO {
    private ClimaActualDTO actual;
    private List<PronosticoDiaDTO> dias = new ArrayList<>();
    private String proveedor;
    private String mensaje;

    public PronosticoCompletoDTO() {
    }

    public PronosticoCompletoDTO(ClimaActualDTO actual, List<PronosticoDiaDTO> dias, String proveedor, String mensaje) {
        this.actual = actual;
        this.dias = dias;
        this.proveedor = proveedor;
        this.mensaje = mensaje;
    }

    public ClimaActualDTO getActual() {
        return actual;
    }

    public void setActual(ClimaActualDTO actual) {
        this.actual = actual;
    }

    public List<PronosticoDiaDTO> getDias() {
        return dias;
    }

    public void setDias(List<PronosticoDiaDTO> dias) {
        this.dias = dias;
    }

    public String getProveedor() {
        return proveedor;
    }

    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
