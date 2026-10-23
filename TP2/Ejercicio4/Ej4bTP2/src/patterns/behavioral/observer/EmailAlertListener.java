package patterns.behavioral.observer;

public class EmailAlertListener implements Observer {
    private final String recipientEmail;

    public EmailAlertListener(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    @Override
    public void update(String event) {
        System.out.println("      -> [EmailAlertListener] Enviando alerta por correo a <" 
                + recipientEmail + ">: " + event);
    }
}
