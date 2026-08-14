package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.entities.Domicilio;
import org.example.entities.Persona;


public class Main {
    static void main() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            //Creamos un domicilio
            Domicilio domicilio = new Domicilio();
            domicilio.setCalle("Av. Siempre Viva 742");
            domicilio.setCiudad("Springfield");

            em.persist(domicilio);

            //Creamos otro domicilio
            Domicilio domicilio2 = new Domicilio();
            domicilio2.setCalle("Av. Siempre Viva 742");
            domicilio2.setCiudad("Springfield");

            em.persist(domicilio2);

            //Creamos una persona
            Persona persona = new Persona();
            persona.setNombre("Homero Simpson");
            persona.setDomicilio(domicilio);
            em.persist(persona);

            //Creamos otra persona
            Persona persona2 = new Persona();
            persona2.setNombre("Jovani Vazquez");
            persona2.setDomicilio(domicilio2);
            em.persist(persona2);

            Persona personaBuscada = em.find(Persona.class, 16L);
            System.out.println(personaBuscada);
            tx.commit();

            System.out.println("El id de domicilio es: " + domicilio.getId());

        }catch (Exception e ) {
            if (tx.isActive()) {
                tx.rollback();
                System.out.println("Transacción revertida por error.");
            }
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
