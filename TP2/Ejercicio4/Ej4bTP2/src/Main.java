import patterns.PatternCategory;
import patterns.PatternRunner;
import patterns.behavioral.observer.ObserverPatternRunner;
import patterns.behavioral.strategy.StrategyPatternRunner;
import patterns.creational.factory.FactoryPatternRunner;
import patterns.creational.singleton.SingletonPatternRunner;
import patterns.structural.adapter.AdapterPatternRunner;
import patterns.structural.decorator.DecoratorPatternRunner;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final List<PatternRunner> patterns = new ArrayList<>();

    static {
        // --- Patrones Creacionales ---
        patterns.add(new FactoryPatternRunner());
        patterns.add(new SingletonPatternRunner());

        // --- Patrones Estructurales ---
        patterns.add(new AdapterPatternRunner());
        patterns.add(new DecoratorPatternRunner());

        // --- Patrones de Comportamiento ---
        patterns.add(new ObserverPatternRunner());
        patterns.add(new StrategyPatternRunner());
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("==========================================================");
        System.out.println("       Catálogo de Patrones de Diseño en Java (GoF)       ");
        System.out.println("==========================================================");

        while (true) {
            printCatalog();

            System.out.print("\n¿Qué patrón querés ver? ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("0") || input.equalsIgnoreCase("salir")) {
                System.out.println("¡Hasta luego!");
                break;
            }

            PatternRunner selectedPattern = findPattern(input);

            if (selectedPattern != null) {
                System.out.println();
                selectedPattern.run();
            } else {
                System.out.println("Opción no válida. Por favor, ingresá el número o nombre del patrón.");
            }
        }

        scanner.close();
    }

    private static void printCatalog() {
        System.out.println("\nPatrones disponibles organizados por categoría:");

        // Agrupamos por la categoría GoF
        Map<PatternCategory, List<PatternRunner>> grouped = patterns.stream()
                .collect(Collectors.groupingBy(PatternRunner::getCategory, LinkedHashMap::new, Collectors.toList()));

        int globalIndex = 1;
        for (Map.Entry<PatternCategory, List<PatternRunner>> entry : grouped.entrySet()) {
            PatternCategory category = entry.getKey();
            System.out.println("\n  [" + category.getDisplayName().toUpperCase() + "]");
            System.out.println("  " + category.getDescription());

            for (PatternRunner runner : entry.getValue()) {
                System.out.printf("    [%d] %-20s - %s%n", globalIndex++, runner.getName(), runner.getDescription());
            }
        }
        System.out.println("\n  [0] Salir");
    }

    private static PatternRunner findPattern(String input) {
        try {
            int index = Integer.parseInt(input) - 1;
            if (index >= 0 && index < patterns.size()) {
                return patterns.get(index);
            }
        } catch (NumberFormatException ignored) {
            // No es número, buscar por coincidencia de texto
        }

        for (PatternRunner runner : patterns) {
            if (runner.getName().toLowerCase().contains(input.toLowerCase())) {
                return runner;
            }
        }

        return null;
    }
}
