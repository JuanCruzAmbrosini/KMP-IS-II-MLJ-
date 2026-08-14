package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.entities.Domicilio;
import org.example.entities.Persona;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            //Creamos domicilio
            Domicilio domicilio = new Domicilio();
            domicilio.setCalle("Wallaby Way 42");
            domicilio.setCiudad("Sydney");

            //Crear Persona y domicilio
            Persona persona = new Persona();
            persona.setNombre("Dory");
            persona.setDomicilio(domicilio);
            em.persist(persona);

            tx.commit();

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
