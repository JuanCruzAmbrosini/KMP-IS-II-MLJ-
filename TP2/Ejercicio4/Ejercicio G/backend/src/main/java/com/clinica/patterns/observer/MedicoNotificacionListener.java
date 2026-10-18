package com.clinica.patterns.observer;

import org.springframework.stereotype.Component;

/**
 * Observador Concreto que reacciona cuando un médico es asignado a un nuevo detalle.
 * Puede enviar notificaciones push, SMS, registrar en consola o guardar en bandeja de entrada médica.
 */
@Component
public class MedicoNotificacionListener implements HistoriaObserver {

    @Override
    public void notificar(NuevoDetalleHistoriaEvent evento) {
        System.out.println("==================================================");
        System.out.println("🔔 [NOTIFICACIÓN AL MÉDICO: " + evento.getMedico().getNombreCompleto() + "]");
        System.out.println("Paciente: " + evento.getPaciente().getApellido() + ", " + evento.getPaciente().getNombre() + " (Doc: " + evento.getPaciente().getDocumento() + ")");
        System.out.println("Fecha de atención: " + evento.getFecha());
        System.out.println("Detalle Clínico registrado: \"" + evento.getDetalle() + "\"");
        System.out.println("Estado: Notificación entregada a la bandeja médica.");
        System.out.println("==================================================");
    }
}
