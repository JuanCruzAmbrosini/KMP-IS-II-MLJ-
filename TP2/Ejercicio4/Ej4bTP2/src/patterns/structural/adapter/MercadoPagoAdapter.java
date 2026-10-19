package patterns.structural.adapter;

// Adaptador (Adapter)
// Convierte la interfaz de una clase en otra interfaz que los clientes esperan.
public class MercadoPagoAdapter implements PaymentProcessor {
    private final LegacyMercadoPagoService legacyService;

    public MercadoPagoAdapter(LegacyMercadoPagoService legacyService) {
        this.legacyService = legacyService;
    }

    @Override
    public void processPayment(double amount) {
        // Adaptación estructural: convierte el contrato de pesos (double) a centavos (long)
        long cents = (long) (amount * 100);
        System.out.println("   [MercadoPagoAdapter] Adaptando contrato: $" + amount + " ARS -> " + cents + " centavos.");
        legacyService.debitAccount(cents);
    }
}
