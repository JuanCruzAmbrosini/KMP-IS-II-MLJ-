package uncuyo.test;

/**
 * Punto de entrada para ejecutar la suite de pruebas unitarias y de integración.
 */
public class MainTest {

    public static void main(String[] args) {
        TestRunner runner = new TestRunner("Suite LectorClientes");

        LectorClientesTest tests = new LectorClientesTest();
        tests.registrarPruebas(runner);

        boolean allPassed = runner.runAll();
        if (!allPassed) {
            System.exit(1);
        }
    }
}
