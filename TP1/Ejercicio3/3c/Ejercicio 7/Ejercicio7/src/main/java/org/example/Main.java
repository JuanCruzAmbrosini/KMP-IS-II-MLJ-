package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.entities.Persona;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {

            //Buscar y mostrar
            Persona encontrada = em.find(Persona.class, 1L);
            if (encontrada != null) {
                System.out.println(encontrada);
            } else  {
                System.out.println("No se encontro el persona");
            }

        } catch (Exception e) {
            if (tx.isActive()){
                tx.rollback();
                System.out.println("Error al cambiar los datos");
            }
            e.printStackTrace();
        }finally {
            em.close();
            emf.close();
        }
    }
}
