package uncuyo.test;

import uncuyo.archivos.Cliente;
import uncuyo.archivos.LectorClientes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Casos de prueba para validar la lectura de archivos y creación de objetos Cliente.
 */
public class LectorClientesTest {

    private final LectorClientes lector = new LectorClientes();

    public void registrarPruebas(TestRunner runner) {
        runner.addTest("testParsearLineaValidaPuntoYComa", this::testParsearLineaValidaPuntoYComa);
        runner.addTest("testParsearLineaConEspacios", this::testParsearLineaConEspacios);
        runner.addTest("testParsearLineaVaciaONula", this::testParsearLineaVaciaONula);
        runner.addTest("testLeerArchivoExistente", this::testLeerArchivoExistente);
        runner.addTest("testLeerArchivoVacio", this::testLeerArchivoVacio);
        runner.addTest("testArchivoInexistenteLanzaExcepcion", this::testArchivoInexistenteLanzaExcepcion);
        runner.addTest("testLecturaClientesOriginales", this::testLecturaClientesOriginales);
    }

    public void testParsearLineaValidaPuntoYComa() {
        String linea = "Lionel;Escaloneta;293851450";
        Cliente cliente = lector.parsearLinea(linea);

        Asserts.assertNotNull(cliente, "El cliente no debería ser nulo");
        Asserts.assertEquals("Lionel", cliente.getNombre(), "Nombre incorrecto");
        Asserts.assertEquals("Escaloneta", cliente.getApellido(), "Apellido incorrecto");
        Asserts.assertEquals("293851450", cliente.getDni(), "DNI incorrecto");
    }

    public void testParsearLineaConEspacios() {
        String linea = "  Pablo ; Perez  ;  293451460  ";
        Cliente cliente = lector.parsearLinea(linea);

        Asserts.assertNotNull(cliente, "El cliente no debería ser nulo");
        Asserts.assertEquals("Pablo", cliente.getNombre(), "El nombre debe eliminar espacios extra");
        Asserts.assertEquals("Perez", cliente.getApellido(), "El apellido debe eliminar espacios extra");
        Asserts.assertEquals("293451460", cliente.getDni(), "El DNI debe eliminar espacios extra");
    }

    public void testParsearLineaVaciaONula() {
        Asserts.assertNull(lector.parsearLinea(""), "Línea vacía debe retornar null");
        Asserts.assertNull(lector.parsearLinea("   "), "Línea de espacios debe retornar null");
        Asserts.assertNull(lector.parsearLinea(null), "Línea nula debe retornar null");
    }

    public void testLeerArchivoExistente() throws IOException {
        File tempFile = File.createTempFile("clientes_test_", ".txt");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("Juan;Perez;11111111\n");
            writer.write("Maria;Gomez;22222222\n");
            writer.write("\n"); // línea vacía
            writer.write("Carlos;Lopez;33333333\n");
        }

        List<Cliente> clientes = lector.leer(tempFile);

        Asserts.assertEquals(3, clientes.size(), "Debería haber leído 3 clientes ignorando la línea vacía");
        Asserts.assertEquals("Juan", clientes.get(0).getNombre(), "Primer cliente incorrecto");
        Asserts.assertEquals("Maria", clientes.get(1).getNombre(), "Segundo cliente incorrecto");
        Asserts.assertEquals("Carlos", clientes.get(2).getNombre(), "Tercer cliente incorrecto");
    }

    public void testLeerArchivoVacio() throws IOException {
        File tempFile = File.createTempFile("clientes_vacio_", ".txt");
        tempFile.deleteOnExit();

        List<Cliente> clientes = lector.leer(tempFile);
        Asserts.assertEquals(0, clientes.size(), "Un archivo vacío debe retornar una lista vacía");
    }

    public void testArchivoInexistenteLanzaExcepcion() {
        Asserts.assertThrows(FileNotFoundException.class, () -> {
            lector.leer("ruta/inexistente/clientes_fantasma.txt");
        }, "Debe lanzar FileNotFoundException para rutas que no existen");
    }

    public void testLecturaClientesOriginales() throws FileNotFoundException {
        String path = "src/uncuyo/archivos/clientes.txt";
        File file = new File(path);

        if (file.exists()) {
            List<Cliente> clientes = lector.leer(file);
            Asserts.assertTrue(!clientes.isEmpty(), "El archivo clientes.txt original debe contener registros");
        }
    }
}
