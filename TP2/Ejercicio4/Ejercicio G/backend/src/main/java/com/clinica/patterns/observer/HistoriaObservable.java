package com.clinica.patterns.observer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sujeto Observable del patrón Observer.
 * Administra los observadores registrados y publica los eventos de novedades clínicas.
 */
@Component
public class HistoriaObservable {

    private final List<HistoriaObserver> observadores = new ArrayList<>();

    public HistoriaObservable(List<HistoriaObserver> observersInyectados) {
        if (observersInyectados != null) {
            this.observadores.addAll(observersInyectados);
        }
    }

    public synchronized void agregarObservador(HistoriaObserver observer) {
        if (observer != null && !observadores.contains(observer)) {
            observadores.add(observer);
        }
    }

    public synchronized void removerObservador(HistoriaObserver observer) {
        observadores.remove(observer);
    }

    public void notificarObservadores(NuevoDetalleHistoriaEvent evento) {
        for (HistoriaObserver obs : observadores) {
            try {
                obs.notificar(evento);
            } catch (Exception e) {
                System.err.println("Error notificando al observador: " + e.getMessage());
            }
        }
    }
}
