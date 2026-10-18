package uncuyo.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Ejecutor simple de pruebas para ejecutar métodos de test y reportar resultados.
 */
public class TestRunner {

    @FunctionalInterface
    public interface TestCaseRunnable {
        void run() throws Throwable;
    }

    private static class TestEntry {
        final String name;
        final TestCaseRunnable runnable;

        TestEntry(String name, TestCaseRunnable runnable) {
            this.name = name;
            this.runnable = runnable;
        }
    }

    private final String suiteName;
    private final List<TestEntry> tests = new ArrayList<>();

    public TestRunner(String suiteName) {
        this.suiteName = suiteName;
    }

    public void addTest(String testName, TestCaseRunnable runnable) {
        tests.add(new TestEntry(testName, runnable));
    }

    public boolean runAll() {
        System.out.println("==================================================");
        System.out.println(" Ejecutando Suite de Pruebas: " + suiteName);
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;
        long startTime = System.currentTimeMillis();

        for (TestEntry test : tests) {
            try {
                test.runnable.run();
                System.out.println("  [PASS] " + test.name);
                passed++;
            } catch (AssertionError e) {
                System.err.println("  [FAIL] " + test.name + " -> " + e.getMessage());
                failed++;
            } catch (Throwable t) {
                System.err.println("  [ERROR] " + test.name + " -> Excepción inesperada: " + t);
                t.printStackTrace(System.err);
                failed++;
            }
        }

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("--------------------------------------------------");
        System.out.println(" Resumen: " + passed + " exitosos, " + failed + " fallidos (" + tests.size() + " total) en " + duration + " ms");
        System.out.println("==================================================\n");

        return failed == 0;
    }
}
