package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.entities.Cliente;
import org.example.entities.Venta;

import java.util.Date;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("    INICIANDO SISTEMA MINIMARKET - JPA   ");
        System.out.println("==================================================\n");

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("MiniMarketPU");
        EntityManager em = emf.createEntityManager();

        try {
            // 1. ALTA
            em.getTransaction().begin();
            System.out.println("[+] FASE 1: REGISTRANDO CLIENTES Y VENTAS...");

            // Cliente 1
            Cliente cliente1 = new Cliente();
            cliente1.setDni(39101222);
            cliente1.setNombre("Luciano");
            cliente1.setApellido("Canovas");
            cliente1.setEmail("lcanovas@ejemplo.com");
            cliente1.setTelefono(261555443);

            // Le creamos dos ventas distintas a Luciano
            Venta venta1 = new Venta();
            venta1.setFecha(new Date());
            venta1.setTotal(85000.0f);
            venta1.setMetodoPago("Tarjeta de Crédito");
            venta1.setCliente(cliente1);

            Venta venta2 = new Venta();
            venta2.setFecha(new Date());
            venta2.setTotal(12500.50f);
            venta2.setMetodoPago("MercadoPago");
            venta2.setCliente(cliente1);

            cliente1.getVentas().add(venta1);
            cliente1.getVentas().add(venta2);

            // Cliente 2
            Cliente cliente2 = new Cliente();
            cliente2.setDni(40333444);
            cliente2.setNombre("Mateo");
            cliente2.setApellido("Peralta");
            cliente2.setEmail("mperalta@ejemplo.com");
            cliente2.setTelefono(261295744);

            Venta venta3 = new Venta();
            venta3.setFecha(new Date());
            venta3.setTotal(45000.0f);
            venta3.setMetodoPago("Transferencia");
            venta3.setCliente(cliente2);
            cliente2.getVentas().add(venta3);

            // Persistimos solo los clientes. Las ventas se guardan solas por el CascadeType.ALL
            em.persist(cliente1);
            em.persist(cliente2);

            em.getTransaction().commit();
            System.out.println("    -> Alta completada con éxito.\n");


            // ----------------------------------------------------------------
            // 2. LECTURA Y NAVEGACIÓN
            // ----------------------------------------------------------------
            System.out.println("CONSULTANDO DATOS Y RELACIONES");
            em.clear();

            Cliente clienteBuscado = em.find(Cliente.class, 39111222);

            if (clienteBuscado != null) {
                System.out.println("    -> Cliente encontrado: " + clienteBuscado.getNombre() + " " + clienteBuscado.getApellido());
                System.out.println("    -> Historial de Ventas:");

                List<Venta> susVentas = clienteBuscado.getVentas();
                float gastoTotal = 0;

                for (Venta v : susVentas) {
                    System.out.println("       * Venta ID " + v.getId() + " | Total: $" + v.getTotal() + " | Pago: " + v.getMetodoPago());
                    gastoTotal += v.getTotal();
                }
                System.out.println("    -> Gasto acumulado del cliente: $" + gastoTotal + "\n");
            }


            // ----------------------------------------------------------------
            // 3. MODIFICACIÓN
            // ----------------------------------------------------------------
            em.getTransaction().begin();
            System.out.println("MODIFICANDO UNA VENTA");

            Cliente clienteModificar = em.find(Cliente.class, 40333444);
            if (clienteModificar != null && !clienteModificar.getVentas().isEmpty()) {
                // Obtenemos su primera (y única) venta y le cambiamos el método de pago
                Venta ventaAModificar = clienteModificar.getVentas().get(0);
                ventaAModificar.setMetodoPago("Efectivo");

                System.out.println("    -> Se actualizó el método de pago de la venta de " + clienteModificar.getNombre() + " a Efectivo.");
            }
            em.getTransaction().commit();
            System.out.println();


            // ----------------------------------------------------------------
            // 4. BAJA EN CASCADA
            // ----------------------------------------------------------------
            em.getTransaction().begin();
            System.out.println("[+] FASE 4: ELIMINACIÓN EN CASCADA...");

            Cliente clienteAEliminar = em.find(Cliente.class, 39111222);
            if (clienteAEliminar != null) {
                em.remove(clienteAEliminar);
                System.out.println("    -> Cliente " + clienteAEliminar.getNombre() + " eliminado.");
                System.out.println("    -> (Revisá la consola SQL: JPA hizo DELETE también en la tabla VENTAS por el CascadeType).");
            }
            em.getTransaction().commit();

            // ----------------------------------------------------------------
            // 5. CONSULTAS AVANZADAS CON JPQL
            // ----------------------------------------------------------------
            System.out.println("\n[+] FASE 5: CONSULTAS AVANZADAS CON JPQL...");

            em.getTransaction().begin();
            Cliente cliente3 = new Cliente();
            cliente3.setDni(41555666);
            cliente3.setNombre("Mateo");
            cliente3.setApellido("Peralta");

            Venta ventaFase5 = new Venta();
            ventaFase5.setFecha(new Date());
            ventaFase5.setTotal(65000.0f);
            ventaFase5.setMetodoPago("Efectivo");
            ventaFase5.setCliente(cliente3);
            cliente3.getVentas().add(ventaFase5);

            em.persist(cliente3);
            em.getTransaction().commit();


            // Todos los clientes ordenados por apellido
            System.out.println("    -> Consulta A: Todos los clientes ordenados por apellido");
            String jpqlClientes = "SELECT c FROM Cliente c ORDER BY c.apellido ASC";

            List<Cliente> listaClientes = em.createQuery(jpqlClientes, Cliente.class).getResultList();

            for (Cliente c : listaClientes) {
                System.out.println("       * " + c.getApellido() + ", " + c.getNombre() + " (DNI: " + c.getDni() + ")");
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error grave en la ejecución: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
            System.out.println("\n==================================================");
            System.out.println("             EJECUCIÓN FINALIZADA                 ");
            System.out.println("==================================================");
        }
    }
}