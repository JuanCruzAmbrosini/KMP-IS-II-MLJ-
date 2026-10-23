package patterns.behavioral.observer;

public class AuditListener implements Observer {
    @Override
    public void update(String event) {
        System.out.println("      -> [AuditListener] Auditoría registrada: " + event);
    }
}
