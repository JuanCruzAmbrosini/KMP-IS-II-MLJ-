package com.clinica.patterns.facade;

import java.util.Date;

public class RegistrarDetalleDTO {
    private String historiaClinicaId;
    private String pacienteId;
    private String medicoId;
    private String usuarioId;
    private String detalleHistoria;
    private Date fechaHistoria;

    public RegistrarDetalleDTO() {
        this.fechaHistoria = new Date();
    }

    public String getHistoriaClinicaId() {
        return historiaClinicaId;
    }

    public void setHistoriaClinicaId(String historiaClinicaId) {
        this.historiaClinicaId = historiaClinicaId;
    }

    public String getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(String pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(String medicoId) {
        this.medicoId = medicoId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getDetalleHistoria() {
        return detalleHistoria;
    }

    public void setDetalleHistoria(String detalleHistoria) {
        this.detalleHistoria = detalleHistoria;
    }

    public Date getFechaHistoria() {
        return fechaHistoria;
    }

    public void setFechaHistoria(Date fechaHistoria) {
        this.fechaHistoria = fechaHistoria;
    }
}
