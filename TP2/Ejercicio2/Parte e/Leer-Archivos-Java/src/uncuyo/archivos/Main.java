package uncuyo.archivos;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<Cliente> clientes = new ArrayList<Cliente>();
        String path = "src/uncuyo/archivos/clientes.txt";

        File file = new File(path);
        LectorClientes lector = new LectorClientes();

        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                Cliente cliente = lector.parsearLinea(linea);
                if (cliente != null) {
                    clientes.add(cliente);
                }
            }
            sc.close();

            clientes.forEach(
                c -> System.out.println(c.toString())
                    );
            System.out.println("Clientes cargados: " + clientes.size());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
