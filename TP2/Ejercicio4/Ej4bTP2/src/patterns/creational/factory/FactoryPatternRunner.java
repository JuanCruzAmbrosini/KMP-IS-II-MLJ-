package patterns.creational.factory;

import patterns.PatternCategory;
import patterns.PatternRunner;

public class FactoryPatternRunner implements PatternRunner {

    @Override
    public PatternCategory getCategory() {
        return PatternCategory.CREACIONAL;
    }

    @Override
    public String getName() {
        return "Factory Method";
    }

    @Override
    public String getDescription() {
        return "Delega la creación de instancias en subclases desacoplando la lógica de negocio de las clases concretas.";
    }

    @Override
    public void run() {
        System.out.println("==========================================================");
        System.out.println(" [CATEGORÍA: " + getCategory().getDisplayName().toUpperCase() + "]");
        System.out.println(" Patrón: " + getName());
        System.out.println(" Propósito: " + getDescription());
        System.out.println("==========================================================");
        System.out.println(">> Naturaleza del patrón en acción:");
        System.out.println("   El cliente trabaja únicamente con 'NotificationService' y la");
        System.out.println("   interfaz 'Notification', sin hacer jamás 'new EmailNotification()'.\n");

        NotificationService emailService = new EmailNotificationService();
        System.out.println("1. Cliente invoca emailService.send(...):");
        emailService.send("Bienvenido al sistema vía correo electrónico.");

        System.out.println("\n2. Cliente invoca smsService.send(...):");
        NotificationService smsService = new SmsNotificationService();
        smsService.send("Tu código de verificación es 123456.");
        
        System.out.println("==========================================================");
    }
}
