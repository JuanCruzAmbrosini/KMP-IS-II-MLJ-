package patterns.creational.factory;

public class EmailNotification implements Notification {
    @Override
    public void notifyUser(String message) {
        System.out.println("Enviando Email: " + message);
    }
}
