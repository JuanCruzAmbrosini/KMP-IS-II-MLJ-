package patterns.creational.factory;

// Creador Concreto para SMS
public class SmsNotificationService extends NotificationService {
    @Override
    protected Notification createNotification() {
        return new SmsNotification();
    }
}
