package com.tp2.ejercicio1.clima.dto;

/**
 * DTO que informa el estado de conectividad con los servicios externos.
 */
public class ApiEstadoDTO {
    private String estado;
    private String proveedorPrimario;
    private Long latenciaMs;
    private String mensaje;
    private String fechaHora;

    public ApiEstadoDTO() {
    }

    public ApiEstadoDTO(String estado, String proveedorPrimario, Long latenciaMs, String mensaje, String fechaHora) {
        this.estado = estado;
        this.proveedorPrimario = proveedorPrimario;
        this.latenciaMs = latenciaMs;
        this.mensaje = mensaje;
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getProveedorPrimario() {
        return proveedorPrimario;
    }

    public void setProveedorPrimario(String proveedorPrimario) {
        this.proveedorPrimario = proveedorPrimario;
    }

    public Long getLatenciaMs() {
        return latenciaMs;
    }

    public void setLatenciaMs(Long latenciaMs) {
        this.latenciaMs = latenciaMs;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(String fechaHora) {
        this.fechaHora = fechaHora;
    }
}
