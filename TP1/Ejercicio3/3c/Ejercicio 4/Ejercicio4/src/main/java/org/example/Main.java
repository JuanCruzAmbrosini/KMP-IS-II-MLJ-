package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.entities.Domicilio;
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
            //Creamos Domicilios
            Domicilio d1 = new Domicilio();
            d1.setCalle("Calle 1");
            d1.setCiudad("Ciudad 1");
            em.persist(d1);

            Domicilio d2 = new Domicilio();
            d2.setCalle("Calle 2");
            d2.setCiudad("Ciudad 2");
            em.persist(d2);

            //Creamos Personas
            Persona p1 = new Persona();
            p1.setNombre("Persona 1");
            List<Domicilio> domicilios = new ArrayList<>();
            domicilios.add(d1);
            domicilios.add(d2);
            p1.setDomicilios(domicilios);
            em.persist(p1);
            tx.commit();
            //Buscar y mostrar
            Persona encontrada = em.find(Persona.class, p1.getId());
            if(encontrada != null) {
                System.out.println("Persona: " +  encontrada.getNombre());
                for (Domicilio domicilio : domicilios) {
                    System.out.println("Domicilio: " +  domicilio.getCalle() + ", " +  domicilio.getCiudad());
                }
            } else  {
                System.out.println("Persona no encontrada");
            }

            //Editamos un domicilio
            Domicilio domEncontrado = em.find(Domicilio.class, 1L);
            domEncontrado.setCiudad("Night City");

            //Eliminamos un domicilio
            Domicilio domEncontrado2 = em.find(Domicilio.class, 2L);
            p1.getDomicilios().remove(domEncontrado2);
            em.remove(domEncontrado2);
            tx.begin();
            tx.commit();

            //Imprimimos persona después de borrar domicilio
            Persona personaEncontrada = em.find(Persona.class, 1L);
            System.out.println("Persona sin un domicilio: " + personaEncontrada);

        } catch (Exception e){
            if (tx.isActive()){
                tx.rollback();
                System.out.println("Error al realizar la consulta en el domicilio.");
            }
            e.printStackTrace();
        }finally {
            em.close();
            emf.close();
        }
    }
}
