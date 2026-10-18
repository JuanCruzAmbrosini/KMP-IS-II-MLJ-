package com.clinica.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OrderBy;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "historia_clinica")
@SQLDelete(sql = "UPDATE historia_clinica SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado = false")
public class HistoriaClinica extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "historiaClinica", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("fechaHistoria DESC")
    private List<DetalleHistoriaClinica> detalles = new ArrayList<>();

    public HistoriaClinica() {
    }

    public HistoriaClinica(Paciente paciente, Usuario usuario) {
        this.paciente = paciente;
        this.usuario = usuario;
    }

    /**
     * Mantiene la integridad bidireccional de la composición
     */
    public void addDetalle(DetalleHistoriaClinica detalle) {
        if (detalle != null) {
            this.detalles.add(detalle);
            detalle.setHistoriaClinica(this);
        }
    }

    public void removeDetalle(DetalleHistoriaClinica detalle) {
        if (detalle != null) {
            this.detalles.remove(detalle);
            detalle.setHistoriaClinica(null);
        }
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetalleHistoriaClinica> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleHistoriaClinica> detalles) {
        this.detalles = detalles;
    }
}
