package patterns.structural.adapter;

// Clase externa/legacy existente incompatible (Adaptee)
public class LegacyMercadoPagoService {
    public void debitAccount(long cents) {
        System.out.println("   [LegacyMercadoPagoService] Debitando centavos vía API legacy: " + cents + "¢");
    }
}
