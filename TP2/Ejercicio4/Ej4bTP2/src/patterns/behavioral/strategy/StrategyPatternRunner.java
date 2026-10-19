package patterns.behavioral.strategy;

import patterns.PatternCategory;
import patterns.PatternRunner;

public class StrategyPatternRunner implements PatternRunner {

    @Override
    public PatternCategory getCategory() {
        return PatternCategory.COMPORTAMIENTO;
    }

    @Override
    public String getName() {
        return "Strategy";
    }

    @Override
    public String getDescription() {
        return "Define una familia de algoritmos, encapsula cada uno de ellos y los hace intercambiables dinámicamente en tiempo de ejecución.";
    }

    @Override
    public void run() {
        System.out.println("==========================================================");
        System.out.println(" [CATEGORÍA: " + getCategory().getDisplayName().toUpperCase() + "]");
        System.out.println(" Patrón: " + getName());
        System.out.println(" Propósito: " + getDescription());
        System.out.println("==========================================================");
        System.out.println(">> Naturaleza del patrón en acción:");
        System.out.println("   El objeto contexto 'NavigatorContext' delega la ejecución del algoritmo");
        System.out.println("   a una estrategia concreta sin acoplarse a ella. La estrategia puede");
        System.out.println("   cambiar en tiempo de ejecución según el contexto o preferencia del usuario.\n");

        String origin = "Buenos Aires";
        String destination = "Bariloche";
        NavigatorContext navigator = new NavigatorContext();

        System.out.println("1. El usuario tiene prisa y elige la ruta más rápida:");
        navigator.setStrategy(new FastestRouteStrategy());
        System.out.println("   Contexto ejecutando buildRoute(\"" + origin + "\", \"" + destination + "\"):");
        navigator.buildRoute(origin, destination);

        System.out.println("\n2. El usuario cambia su preferencia a un viaje turístico:");
        navigator.setStrategy(new ScenicRouteStrategy());
        System.out.println("   Contexto ejecutando buildRoute(\"" + origin + "\", \"" + destination + "\"):");
        navigator.buildRoute(origin, destination);

        System.out.println("\n3. El usuario activa el modo ecológico para ahorrar combustible:");
        navigator.setStrategy(new EcoRouteStrategy());
        System.out.println("   Contexto ejecutando buildRoute(\"" + origin + "\", \"" + destination + "\"):");
        navigator.buildRoute(origin, destination);

        System.out.println("==========================================================");
    }
}
