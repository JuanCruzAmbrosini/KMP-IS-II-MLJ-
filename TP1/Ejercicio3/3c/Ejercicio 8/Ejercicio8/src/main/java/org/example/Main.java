package org.example;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.example.entities.Curso;
import org.example.entities.Domicilio;
import org.example.entities.Persona;

import java.util.List;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
        EntityManager em = emf.createEntityManager();

        cargarDatosIniciales(em);

        //Ejemplo1: Personas ordenadas por nombre
        System.out.println("Ejemplo 1: Personas ordenadas por nombre");
        List<Persona> personasOrdenadas = em.createQuery(
                "SELECT p FROM Persona p ORDER BY p.nombre ASC", Persona.class
        ).getResultList();

        personasOrdenadas.forEach(System.out::println);

        //Ejemplo2: Personas filtradas por lugar
        System.out.println("Ejemplo 2: Personas filtradas por lugar");
        List<Persona> personasPorCiudad = em.createQuery(
                        "SELECT p FROM Persona p JOIN p.domicilio d WHERE d.ciudad = :ciudad", Persona.class)
                .setParameter("ciudad", "Springfield")
                .getResultList();
        personasPorCiudad.forEach(System.out::println);

        //Ejemplo 3: Cursos con más de N inscriptos
        System.out.println("Ejemplo 3: Curosos con más de N inscriptos");
        Long minimo = 1L;
        List<Curso> cursosPopulares = em.createQuery(
                        "SELECT c FROM Curso c WHERE SIZE(c.inscriptos) > :minimo", Curso.class)
                .setParameter("minimo", minimo).getResultList();
        cursosPopulares.forEach(c -> System.out.println(c.getNombre() + " -> inscriptos: " +  c.getInscriptos().size()));

        //Ejemplo4: Uso de NamedQuery
        System.out.println("Ejemplo 4: Personas por ciudad (namedQuery)");
        List<Persona> personasSpringfield = em.createNamedQuery("Persona.buscarPorCiudad", Persona.class)
                .setParameter("ciudad", "Springfield").getResultList();

        personasSpringfield.forEach(System.out::println);

        em.close();
        emf.close();
    }

    private static void cargarDatosIniciales(EntityManager em){
        em.getTransaction().begin();

        //domicilios
        Domicilio d1 = new Domicilio("Av. Siempre Viva", "Springfield");
        Domicilio d2 = new Domicilio("Calle 123", "Hoja");

        //Cursos
        Curso Java = new Curso("Java Básico");
        Curso Python = new Curso("Python");
        Curso Rubi = new Curso("Rubi");

        //Perosnas
        Persona p1 = new Persona("Homero");
        p1.setDomicilio(d1);
        p1.agregarCurso(Java);
        p1.agregarCurso(Python);

        Persona p2 = new Persona("Miguel");
        p2.setDomicilio(d2);
        p2.agregarCurso(Rubi);

        em.persist(p1);
        em.persist(p2);

        em.getTransaction().commit();
    }

}
