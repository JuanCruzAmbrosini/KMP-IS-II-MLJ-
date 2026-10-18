package uncuyo.archivos;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class LectorClientes {

    /**
     * Lee un archivo desde la ruta proporcionada y retorna la lista de clientes.
     */
    public List<Cliente> leer(String path) throws FileNotFoundException {
        if (path == null) {
            throw new IllegalArgumentException("La ruta del archivo no puede ser nula");
        }
        return leer(new File(path));
    }

    /**
     * Lee un objeto File y retorna la lista de clientes.
     */
    public List<Cliente> leer(File file) throws FileNotFoundException {
        if (file == null) {
            throw new IllegalArgumentException("El archivo no puede ser nulo");
        }
        if (!file.exists()) {
            throw new FileNotFoundException("El archivo no existe: " + file.getPath());
        }

        List<Cliente> clientes = new ArrayList<>();
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String linea = sc.nextLine().trim();
                if (!linea.isEmpty()) {
                    Cliente c = parsearLinea(linea);
                    if (c != null) {
                        clientes.add(c);
                    }
                }
            }
        }
        return clientes;
    }

    /**
     * Parsea una línea delimitada por ';' o ',' utilizando StringTokenizer.
     * Formato esperado: NOMBRE;APELLIDO;DNI
     */
    public Cliente parsearLinea(String linea) {
        if (linea == null || linea.trim().isEmpty()) {
            return null;
        }

        StringTokenizer tokenizer = new StringTokenizer(linea, ";,");
        if (!tokenizer.hasMoreTokens()) {
            return null;
        }

        String nombre = tokenizer.nextToken().trim();
        String apellido = tokenizer.hasMoreTokens() ? tokenizer.nextToken().trim() : "";
        String dni = tokenizer.hasMoreTokens() ? tokenizer.nextToken().trim() : "";

        return new Cliente(nombre, apellido, dni);
    }
}
