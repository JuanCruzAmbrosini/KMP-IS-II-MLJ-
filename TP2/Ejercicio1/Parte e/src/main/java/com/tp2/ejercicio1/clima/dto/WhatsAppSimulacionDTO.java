package com.tp2.ejercicio1.clima.dto;

import java.util.Map;

/**
 * DTO para demostrar y simular peticiones hacia la WhatsApp Business Cloud API (Meta Graph API)
 * usando RestTemplate.
 */
public class WhatsAppSimulacionDTO {
    private String destinatario;
    private String tipoMensaje; // "text" o "template"
    private String plantillaNombre;
    private String cuerpoMensaje;
    private boolean exito;
    private String mensajeId;
    private String respuestaRaw;
    private String explicacionTecnica;

    public WhatsAppSimulacionDTO() {
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getTipoMensaje() {
        return tipoMensaje;
    }

    public void setTipoMensaje(String tipoMensaje) {
        this.tipoMensaje = tipoMensaje;
    }

    public String getPlantillaNombre() {
        return plantillaNombre;
    }

    public void setPlantillaNombre(String plantillaNombre) {
        this.plantillaNombre = plantillaNombre;
    }

    public String getCuerpoMensaje() {
        return cuerpoMensaje;
    }

    public void setCuerpoMensaje(String cuerpoMensaje) {
        this.cuerpoMensaje = cuerpoMensaje;
    }

    public boolean isExito() {
        return exito;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    public String getMensajeId() {
        return mensajeId;
    }

    public void setMensajeId(String mensajeId) {
        this.mensajeId = mensajeId;
    }

    public String getRespuestaRaw() {
        return respuestaRaw;
    }

    public void setRespuestaRaw(String respuestaRaw) {
        this.respuestaRaw = respuestaRaw;
    }

    public String getExplicacionTecnica() {
        return explicacionTecnica;
    }

    public void setExplicacionTecnica(String explicacionTecnica) {
        this.explicacionTecnica = explicacionTecnica;
    }
}
