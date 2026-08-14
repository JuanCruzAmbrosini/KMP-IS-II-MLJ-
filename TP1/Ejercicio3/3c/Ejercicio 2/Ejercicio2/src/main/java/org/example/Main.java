package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.entities.Producto;

import java.util.ArrayList;
import java.util.List;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
            EntityManager em = emf.createEntityManager();

            //Estado nuevo (Transient)
            Producto producto = new Producto("Laptop", 1200.0);
            System.out.println("Estado NUEVO (Transient): " + producto);

            //De NUEVO a GESTIONADO (persist)
            em.getTransaction().begin();
            em.persist(producto);
            em.getTransaction().commit();
            System.out.println("Estado GESTIONADO (Persistent): " + producto);

            //Cambios en estado GESTIONADO
            em.getTransaction().begin();
            producto.setPrecio(1100.0);  //Debería ser detectado
            em.getTransaction().commit();
            System.out.println("Cambio en estado GESTIONADO: " + producto);

            //De GESTIONADO a DESASOCIADO (Detached)
            em.close();  //Cerramos el EntityManager
            System.out.println("Estado DESASOCIADO (Detached): " + producto);
            producto.setPrecio(999.0);
            System.out.println("Cambio en estado Detached (NO SE GUARDA): " + producto);

            //De DESASOCIADO a GESTIONADO (merge)
            EntityManager em2 = emf.createEntityManager();
            em2.getTransaction().begin();
            Producto productoGestionado = em2.merge(producto); //Vuelve a gestionado
            em2.getTransaction().commit();
            System.out.println("Estado GESTIONADO otra vez (con merge) " +  productoGestionado);

            //Estado ELIMINADO (Removed)
            em2.getTransaction().begin();
            em2.remove(productoGestionado); //Marcado para eliminar
            em2.getTransaction().commit();
            System.out.println("Estado ELIMINADO (Removed): Producto eliminado de la base de datos.");
            em2.close();
            emf.close();
    }
}
