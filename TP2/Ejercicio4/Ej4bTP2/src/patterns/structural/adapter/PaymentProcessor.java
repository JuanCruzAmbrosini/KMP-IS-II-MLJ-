package patterns.structural.adapter;

// Interfaz esperada por el sistema (Target)
public interface PaymentProcessor {
    void processPayment(double amount);
}
