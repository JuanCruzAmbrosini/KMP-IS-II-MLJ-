package uncuyo.test;

import java.util.Objects;

/**
 * Utilidades de aserción mínimas para pruebas unitarias sin dependencias externas.
 */
public class Asserts {

    @FunctionalInterface
    public interface Executable {
        void execute() throws Throwable;
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message != null ? message : "Se esperaba true pero fue false");
        }
    }

    public static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message != null ? message : "Se esperaba false pero fue true");
        }
    }

    public static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            String detail = "Esperado: <" + expected + ">, pero fue: <" + actual + ">";
            throw new AssertionError(message != null ? message + " -> " + detail : detail);
        }
    }

    public static void assertNotNull(Object object, String message) {
        if (object == null) {
            throw new AssertionError(message != null ? message : "El objeto no debería ser null");
        }
    }

    public static void assertNull(Object object, String message) {
        if (object != null) {
            throw new AssertionError(message != null ? message : "El objeto debería ser null pero fue: " + object);
        }
    }

    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable, String message) {
        try {
            executable.execute();
        } catch (Throwable actualThrown) {
            if (expectedType.isInstance(actualThrown)) {
                return expectedType.cast(actualThrown);
            }
            throw new AssertionError((message != null ? message + " -> " : "") +
                    "Se esperaba excepción de tipo " + expectedType.getName() +
                    " pero se lanzó " + actualThrown.getClass().getName());
        }
        throw new AssertionError((message != null ? message + " -> " : "") +
                "Se esperaba excepción de tipo " + expectedType.getName() +
                " pero no se lanzó ninguna excepción");
    }
}
