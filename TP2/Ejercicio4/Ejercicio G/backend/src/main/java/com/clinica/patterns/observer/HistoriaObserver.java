package com.clinica.patterns.observer;

/**
 * PATRÓN DE COMPORTAMIENTO: Observer
 * Interfaz suscriptora para entidades interesadas en eventos clínicos.
 */
public interface HistoriaObserver {
    void notificar(NuevoDetalleHistoriaEvent evento);
}
