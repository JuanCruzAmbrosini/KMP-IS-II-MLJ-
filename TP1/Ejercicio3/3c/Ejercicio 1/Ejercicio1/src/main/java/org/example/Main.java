package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.entities.Persona;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main() {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
        EntityManager em = emf.createEntityManager();
        Persona p1 = new Persona();
        p1.setNombre("Juan Cruz Ambrosini");
//        em.getTransaction().begin();
//        em.persist(p1);
//        em.getTransaction().commit();
//        System.out.println("Persona 1: " +  p1.getId());

        Long idABuscar = 1L;
        Persona personaEncontrada = em.find(Persona.class, idABuscar);

        if (personaEncontrada != null) {
            System.out.println("Persona encontrada " + personaEncontrada.getNombre());
        } else {
            System.out.println("No existe persona encontrada " + idABuscar);
        }

        em.close();
        emf.close();

    }
}
