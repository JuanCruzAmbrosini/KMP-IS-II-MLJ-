package patterns.creational.factory;

public class SmsNotification implements Notification {
    @Override
    public void notifyUser(String message) {
        System.out.println("Enviando SMS: " + message);
    }
}
