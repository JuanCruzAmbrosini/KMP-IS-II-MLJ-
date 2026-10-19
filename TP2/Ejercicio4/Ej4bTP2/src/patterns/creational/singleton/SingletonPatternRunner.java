package patterns.creational.singleton;

import patterns.PatternCategory;
import patterns.PatternRunner;

public class SingletonPatternRunner implements PatternRunner {

    @Override
    public PatternCategory getCategory() {
        return PatternCategory.CREACIONAL;
    }

    @Override
    public String getName() {
        return "Singleton";
    }

    @Override
    public String getDescription() {
        return "Garantiza que una clase tenga una única instancia global y proporciona un punto de acceso global seguro.";
    }

    @Override
    public void run() {
        System.out.println("==========================================================");
        System.out.println(" [CATEGORÍA: " + getCategory().getDisplayName().toUpperCase() + "]");
        System.out.println(" Patrón: " + getName() + " (Double-Checked Locking)");
        System.out.println(" Propósito: " + getDescription());
        System.out.println("==========================================================");
        System.out.println(">> Naturaleza del patrón en acción:");
        System.out.println("   Control estricto sobre el ciclo de vida e instanciación única.\n");

        System.out.println("1. Cliente A solicita la conexión (DatabaseConnectionPool.getInstance()):");
        DatabaseConnectionPool pool1 = DatabaseConnectionPool.getInstance();
        pool1.executeQuery("SELECT * FROM users");

        System.out.println("\n2. Cliente B solicita la conexión (DatabaseConnectionPool.getInstance()):");
        DatabaseConnectionPool pool2 = DatabaseConnectionPool.getInstance();
        pool2.executeQuery("SELECT * FROM orders");

        System.out.println("\n3. Verificación de identidad en memoria (pool1 == pool2):");
        boolean same = (pool1 == pool2);
        System.out.println("   ¿Misma referencia de memoria? -> " + same);
        System.out.println("   Identidad pool1: " + System.identityHashCode(pool1));
        System.out.println("   Identidad pool2: " + System.identityHashCode(pool2));
        System.out.println("   -> Se comprueba que NO se crearon dos pools, compartiendo el mismo recurso.");
        System.out.println("==========================================================");
    }
}
