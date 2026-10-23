package patterns.structural.adapter;

import patterns.PatternCategory;
import patterns.PatternRunner;

public class AdapterPatternRunner implements PatternRunner {

    @Override
    public PatternCategory getCategory() {
        return PatternCategory.ESTRUCTURAL;
    }

    @Override
    public String getName() {
        return "Adapter";
    }

    @Override
    public String getDescription() {
        return "Permite que colaboren objetos con interfaces incompatibles, actuando como un puente traductor entre ambos.";
    }

    @Override
    public void run() {
        System.out.println("==========================================================");
        System.out.println(" [CATEGORÍA: " + getCategory().getDisplayName().toUpperCase() + "]");
        System.out.println(" Patrón: " + getName());
        System.out.println(" Propósito: " + getDescription());
        System.out.println("==========================================================");
        System.out.println(">> Naturaleza del patrón en acción:");
        System.out.println("   Tenemos una clase externa/legacy 'LegacyMercadoPagoService' cuyo método es:");
        System.out.println("     void debitAccount(long cents)");
        System.out.println("   Pero nuestro sistema cliente SOLO sabe interactuar con:");
        System.out.println("     PaymentProcessor.processPayment(double amount)\n");
        System.out.println("   El Adapter envuelve la clase incompatible y adapta la estructura de datos y métodos.\n");

        System.out.println("1. Instanciamos el servicio legacy existente (Adaptee):");
        LegacyMercadoPagoService legacyService = new LegacyMercadoPagoService();

        System.out.println("\n2. Lo envolvemos en el adaptador que implementa nuestra interfaz destino (Target):");
        PaymentProcessor processor = new MercadoPagoAdapter(legacyService);

        System.out.println("\n3. El cliente efectúa el pago sin enterarse de las diferencias de interfaz:");
        double purchaseAmount = 1499.50;
        System.out.println("   Cliente llama: processor.processPayment(" + purchaseAmount + ")");
        processor.processPayment(purchaseAmount);

        System.out.println("\n4. Otro pago de prueba:");
        processor.processPayment(250.00);

        System.out.println("==========================================================");
    }
}
