package com.clinica.patterns.observer;

import com.clinica.model.DetalleHistoriaClinica;
import com.clinica.model.Medico;
import com.clinica.model.Paciente;

import java.util.Date;

/**
 * Evento que encapsula los datos cuando un médico es vinculado a una nueva evolución médica.
 */
public class NuevoDetalleHistoriaEvent {

    private final Paciente paciente;
    private final Medico medico;
    private final String detalle;
    private final Date fecha;

    public NuevoDetalleHistoriaEvent(Paciente paciente, Medico medico, String detalle, Date fecha) {
        this.paciente = paciente;
        this.medico = medico;
        this.detalle = detalle;
        this.fecha = fecha;
    }

    public static NuevoDetalleHistoriaEvent desdeDetalle(Paciente paciente, DetalleHistoriaClinica detalle) {
        return new NuevoDetalleHistoriaEvent(
                paciente,
                detalle.getMedico(),
                detalle.getDetalleHistoria(),
                detalle.getFechaHistoria()
        );
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public String getDetalle() {
        return detalle;
    }

    public Date getFecha() {
        return fecha;
    }
}
