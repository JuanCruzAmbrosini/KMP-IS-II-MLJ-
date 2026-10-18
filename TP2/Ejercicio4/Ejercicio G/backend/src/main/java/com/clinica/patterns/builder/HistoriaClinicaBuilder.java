package com.clinica.patterns.builder;

import com.clinica.model.DetalleHistoriaClinica;
import com.clinica.model.HistoriaClinica;
import com.clinica.model.Medico;
import com.clinica.model.Paciente;
import com.clinica.model.Usuario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * PATRÓN CREACIONAL: Builder
 * 
 * Permite la construcción paso a paso de una HistoriaClinica compleja garantizando
 * su estado consistente e inmutable antes de la persistencia (asociando Paciente,
 * Usuario autor y sus correspondientes DetalleHistoriaClinica con sus respectivos Médicos).
 */
public class HistoriaClinicaBuilder {

    private Paciente paciente;
    private Usuario usuario;
    private final List<DetalleHistoriaClinica> detalles = new ArrayList<>();

    public static HistoriaClinicaBuilder builder() {
        return new HistoriaClinicaBuilder();
    }

    public HistoriaClinicaBuilder conPaciente(Paciente paciente) {
        this.paciente = paciente;
        return this;
    }

    public HistoriaClinicaBuilder creadoPor(Usuario usuario) {
        this.usuario = usuario;
        return this;
    }

    public HistoriaClinicaBuilder agregarDetalle(Medico medico, String textoDetalle, Date fecha) {
        if (medico == null) {
            throw new IllegalArgumentException("No se puede registrar un detalle sin un médico asignado.");
        }
        if (textoDetalle == null || textoDetalle.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto del detalle médico no puede estar vacío.");
        }
        DetalleHistoriaClinica detalle = new DetalleHistoriaClinica();
        detalle.setMedico(medico);
        detalle.setDetalleHistoria(textoDetalle.trim());
        detalle.setFechaHistoria(fecha != null ? fecha : new Date());
        this.detalles.add(detalle);
        return this;
    }

    public HistoriaClinicaBuilder agregarDetalle(Medico medico, String textoDetalle) {
        return agregarDetalle(medico, textoDetalle, new Date());
    }

    public HistoriaClinica build() {
        if (this.paciente == null) {
            throw new IllegalStateException("No se puede construir una HistoriaClinica sin asignar un Paciente.");
        }
        if (this.usuario == null) {
            throw new IllegalStateException("No se puede construir una HistoriaClinica sin registrar el Usuario autor.");
        }

        HistoriaClinica historiaClinica = new HistoriaClinica(this.paciente, this.usuario);
        for (DetalleHistoriaClinica det : this.detalles) {
            historiaClinica.addDetalle(det);
        }
        return historiaClinica;
    }
}
