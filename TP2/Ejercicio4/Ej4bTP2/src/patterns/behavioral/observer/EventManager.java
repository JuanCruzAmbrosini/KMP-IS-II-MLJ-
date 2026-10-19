package patterns.behavioral.observer;

import java.util.ArrayList;
import java.util.List;

public class EventManager {
    private final List<Observer> listeners = new ArrayList<>();

    public void subscribe(Observer listener) {
        listeners.add(listener);
        System.out.println("   [EventManager] Nuevo suscriptor registrado: " + listener.getClass().getSimpleName());
    }

    public void unsubscribe(Observer listener) {
        listeners.remove(listener);
        System.out.println("   [EventManager] Suscriptor removido: " + listener.getClass().getSimpleName());
    }

    public void notify(String event) {
        System.out.println("   [EventManager] Notificando evento a " + listeners.size() + " observador(es)...");
        for (Observer listener : listeners) {
            listener.update(event);
        }
    }
}
