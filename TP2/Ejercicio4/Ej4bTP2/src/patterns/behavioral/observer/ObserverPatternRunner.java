package patterns.behavioral.observer;

import patterns.PatternCategory;
import patterns.PatternRunner;

public class ObserverPatternRunner implements PatternRunner {

    @Override
    public PatternCategory getCategory() {
        return PatternCategory.COMPORTAMIENTO;
    }

    @Override
    public String getName() {
        return "Observer";
    }

    @Override
    public String getDescription() {
        return "Establece un mecanismo de suscripción para notificar automáticamente a múltiples objetos sobre los eventos que le suceden al objeto que están observando.";
    }

    @Override
    public void run() {
        System.out.println("==========================================================");
        System.out.println(" [CATEGORÍA: " + getCategory().getDisplayName().toUpperCase() + "]");
        System.out.println(" Patrón: " + getName() + " (Publicador - Suscriptor)");
        System.out.println(" Propósito: " + getDescription());
        System.out.println("==========================================================");
        System.out.println(">> Naturaleza del patrón en acción:");
        System.out.println("   Desacoplamiento en la comunicación: el publicador 'EventManager'");
        System.out.println("   no conoce los tipos concretos de suscriptores ni cuántos son;");
        System.out.println("   solo sabe que implementan el método 'update(String event)'.\n");

        EventManager eventManager = new EventManager();

        Observer auditLog = new AuditListener();
        Observer securityAlert = new EmailAlertListener("seguridad@empresa.com");

        System.out.println("1. Suscribiendo observadores al gestor de eventos:");
        eventManager.subscribe(auditLog);
        eventManager.subscribe(securityAlert);

        System.out.println("\n2. Ocurre un evento en el sistema (ej. inicio de sesión fallido):");
        eventManager.notify("EVENTO_001: Múltiples intentos fallidos de login para el usuario 'admin'");

        System.out.println("\n3. Desuscribiendo a un observador (EmailAlertListener) dinámicamente:");
        eventManager.unsubscribe(securityAlert);

        System.out.println("\n4. Ocurre un segundo evento (ej. exportación de reporte):");
        eventManager.notify("EVENTO_002: Exportación de base de datos completada con éxito");

        System.out.println("==========================================================");
    }
}
