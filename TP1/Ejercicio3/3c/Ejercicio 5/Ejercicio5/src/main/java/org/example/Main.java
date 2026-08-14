package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.entities.Curso;
import org.example.entities.Persona;


import java.util.ArrayList;
import java.util.List;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Curso curso1 = new Curso("Java Básico");
            Curso curso2 = new Curso("Base de Datos");
            em.persist(curso1);
            em.persist(curso2);

            Persona p1 = new Persona("Marcos");
            p1.agregarCurso(curso1);
            p1.agregarCurso(curso2);
            em.persist(p1);
            tx.commit();

            System.out.println("Persona y cursos persistidos en memoria");

            //Mostrar

            System.out.println(em.find(Persona.class, 1L));
            Curso curso = em.find(Curso.class, 1L);
            for (Persona p : curso.getInscriptos()) {
                System.out.println("Persona: " + p.getNombre());
            }

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
                System.out.println("Error en el transaction");

            }
            e.printStackTrace();
        }finally {
            em.close();
            emf.close();
        }
    }
}
