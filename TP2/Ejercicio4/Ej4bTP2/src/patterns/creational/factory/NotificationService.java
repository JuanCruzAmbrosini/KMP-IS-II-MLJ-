package patterns.creational.factory;

// Creador Abstracto
public abstract class NotificationService {
    public void send(String message) {
        Notification notification = createNotification();
        notification.notifyUser(message);
    }
    protected abstract Notification createNotification(); // Factory Method
}
