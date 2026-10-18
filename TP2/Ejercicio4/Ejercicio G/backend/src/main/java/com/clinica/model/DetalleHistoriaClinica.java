package com.clinica.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.Date;

@Entity
@Table(name = "detalle_historia_clinica")
@SQLDelete(sql = "UPDATE detalle_historia_clinica SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado = false")
public class DetalleHistoriaClinica extends BaseEntity {

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha_historia", nullable = false)
    private Date fechaHistoria;

    @Column(name = "detalle_historia", nullable = false, length = 4000)
    private String detalleHistoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historia_clinica_id", nullable = false)
    @JsonIgnore
    private HistoriaClinica historiaClinica;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    public DetalleHistoriaClinica() {
        this.fechaHistoria = new Date();
    }

    public DetalleHistoriaClinica(Date fechaHistoria, String detalleHistoria, Medico medico) {
        this.fechaHistoria = (fechaHistoria != null) ? fechaHistoria : new Date();
        this.detalleHistoria = detalleHistoria;
        this.medico = medico;
    }

    public Date getFechaHistoria() {
        return fechaHistoria;
    }

    public void setFechaHistoria(Date fechaHistoria) {
        this.fechaHistoria = fechaHistoria;
    }

    public String getDetalleHistoria() {
        return detalleHistoria;
    }

    public void setDetalleHistoria(String detalleHistoria) {
        this.detalleHistoria = detalleHistoria;
    }

    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }
}
