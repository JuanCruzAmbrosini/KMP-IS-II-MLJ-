package org.example;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.example.entities.Curso;
import org.example.entities.Domicilio;
import org.example.entities.Persona;

import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {
//----------------------------------------------------------------------------------------------------------------------
//Ejercicio Número 1:
// ----------------------------------------------------------------------------------------------------------------------


//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
//        EntityManager em = emf.createEntityManager();
//        Persona p1 = new Persona();
//        p1.setNombre("Juan Cruz Ambrosini");
////        em.getTransaction().begin();
////        em.persist(p1);
////        em.getTransaction().commit();
////        System.out.println("Persona 1: " +  p1.getId());
//
//        Long idABuscar = 1L;
//        Persona personaEncontrada = em.find(Persona.class, idABuscar);
//
//        if (personaEncontrada != null) {
//            System.out.println("Persona encontrada " + personaEncontrada.getNombre());
//        } else {
//            System.out.println("No existe persona encontrada " + idABuscar);
//        }
//
//        em.close();
//        emf.close();

//----------------------------------------------------------------------------------------------------------------------
//Ejercicio Número 2:
// ----------------------------------------------------------------------------------------------------------------------

//            EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
//            EntityManager em = emf.createEntityManager();
//
//            //Estado nuevo (Transient)
//            Producto producto = new Producto("Laptop", 1200.0);
//            System.out.println("Estado NUEVO (Transient): " + producto);
//
//            //De NUEVO a GESTIONADO (persist)
//            em.getTransaction().begin();
//            em.persist(producto);
//            em.getTransaction().commit();
//            System.out.println("Estado GESTIONADO (Persistent): " + producto);
//
//            //Cambios en estado GESTIONADO
//            em.getTransaction().begin();
//            producto.setPrecio(1100.0);  //Debería ser detectado
//            em.getTransaction().commit();
//            System.out.println("Cambio en estado GESTIONADO: " + producto);
//
//            //De GESTIONADO a DESASOCIADO (Detached)
//            em.close();  //Cerramos el EntityManager
//            System.out.println("Estado DESASOCIADO (Detached): " + producto);
//            producto.setPrecio(999.0);
//            System.out.println("Cambio en estado Detached (NO SE GUARDA): " + producto);
//
//            //De DESASOCIADO a GESTIONADO (merge)
//            EntityManager em2 = emf.createEntityManager();
//            em2.getTransaction().begin();
//            Producto productoGestionado = em2.merge(producto); //Vuelve a gestionado
//            em2.getTransaction().commit();
//            System.out.println("Estado GESTIONADO otra vez (con merge) " +  productoGestionado);
//
//            //Estado ELIMINADO (Removed)
//            em2.getTransaction().begin();
//            em2.remove(productoGestionado); //Marcado para eliminar
//            em2.getTransaction().commit();
//            System.out.println("Estado ELIMINADO (Removed): Producto eliminado de la base de datos.");
//            em2.close();
//            emf.close();

//----------------------------------------------------------------------------------------------------------------------
//Ejercicio Número 3:
// ----------------------------------------------------------------------------------------------------------------------

//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
//        EntityManager em = emf.createEntityManager();
//
//        EntityTransaction tx = em.getTransaction();
//
//        try {
//            tx.begin();
//
//            //Creamos un domicilio
//            Domicilio domicilio = new Domicilio();
//            domicilio.setCalle("Av. Siempre Viva 742");
//            domicilio.setCiudad("Springfield");
//
//            em.persist(domicilio);
//
//            //Creamos otro domicilio
//            Domicilio domicilio2 = new Domicilio();
//            domicilio2.setCalle("Av. Siempre Viva 742");
//            domicilio2.setCiudad("Springfield");
//
//            em.persist(domicilio2);
//
//            //Creamos una persona
//            Persona persona = new Persona();
//            persona.setNombre("Homero Simpson");
//            persona.setDomicilio(domicilio);
//            em.persist(persona);
//
//            //Creamos otra persona
//            Persona persona2 = new Persona();
//            persona2.setNombre("Jovani Vazquez");
//            persona2.setDomicilio(domicilio2);
//            em.persist(persona2);
//
//            Persona personaBuscada = em.find(Persona.class, 16L);
//            System.out.println(personaBuscada);
//            tx.commit();
//
//            System.out.println("El id de domicilio es: " + domicilio.getId());
//
//        }catch (Exception e ) {
//            if (tx.isActive()) {
//                tx.rollback();
//                System.out.println("Transacción revertida por error.");
//            }
//            e.printStackTrace();
//        } finally {
//            em.close();
//            emf.close();
//        }

//----------------------------------------------------------------------------------------------------------------------
//Ejercicio Número 4:
// ----------------------------------------------------------------------------------------------------------------------

//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
//        EntityManager em = emf.createEntityManager();
//        EntityTransaction tx = em.getTransaction();
//        try {
//            tx.begin();
//            //Creamos Domicilios
//            Domicilio d1 = new Domicilio();
//            d1.setCalle("Calle 1");
//            d1.setCiudad("Ciudad 1");
//            em.persist(d1);
//
//            Domicilio d2 = new Domicilio();
//            d2.setCalle("Calle 2");
//            d2.setCiudad("Ciudad 2");
//            em.persist(d2);
//
//            //Creamos Personas
//            Persona p1 = new Persona();
//            p1.setNombre("Persona 1");
//            List<Domicilio> domicilios = new ArrayList<>();
//            domicilios.add(d1);
//            domicilios.add(d2);
//            p1.setDomicilios(domicilios);
//            em.persist(p1);
//            tx.commit();
//            //Buscar y mostrar
//            Persona encontrada = em.find(Persona.class, p1.getId());
//            if(encontrada != null) {
//                System.out.println("Persona: " +  encontrada.getNombre());
//                for (Domicilio domicilio : domicilios) {
//                    System.out.println("Domicilio: " +  domicilio.getCalle() + ", " +  domicilio.getCiudad());
//                }
//            } else  {
//                System.out.println("Persona no encontrada");
//            }
//
//            //Editamos un domicilio
//            Domicilio domEncontrado = em.find(Domicilio.class, 1L);
//            domEncontrado.setCiudad("Night City");
//
//            //Eliminamos un domicilio
//            Domicilio domEncontrado2 = em.find(Domicilio.class, 2L);
//            p1.getDomicilios().remove(domEncontrado2);
//            em.remove(domEncontrado2);
//            tx.begin();
//            tx.commit();
//
//            //Imprimimos persona después de borrar domicilio
//            Persona personaEncontrada = em.find(Persona.class, 1L);
//            System.out.println("Persona sin un domicilio: " + personaEncontrada);
//
//        } catch (Exception e){
//            if (tx.isActive()){
//                tx.rollback();
//                System.out.println("Error al realizar la consulta en el domicilio.");
//            }
//            e.printStackTrace();
//        }finally {
//            em.close();
//            emf.close();
//        }

//----------------------------------------------------------------------------------------------------------------------
//Ejercicio Número 5:
// ----------------------------------------------------------------------------------------------------------------------

//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
//        EntityManager em = emf.createEntityManager();
//        EntityTransaction tx = em.getTransaction();
//        try {
//            tx.begin();
//            Curso curso1 = new Curso("Java Básico");
//            Curso curso2 = new Curso("Base de Datos");
//            em.persist(curso1);
//            em.persist(curso2);
//
//            Persona p1 = new Persona("Marcos");
//            p1.agregarCurso(curso1);
//            p1.agregarCurso(curso2);
//            em.persist(p1);
//            tx.commit();
//
//            System.out.println("Persona y cursos persistidos en memoria");
//
//            //Mostrar
//
//            System.out.println(em.find(Persona.class, 1L));
//            Curso curso = em.find(Curso.class, 1L);
//            for (Persona p : curso.getInscriptos()) {
//                System.out.println("Persona: " + p.getNombre());
//            }
//
//        } catch (Exception e) {
//            if (tx.isActive()) {
//                tx.rollback();
//                System.out.println("Error en el transaction");
//
//            }
//            e.printStackTrace();
//        }finally {
//            em.close();
//            emf.close();
//        }

//----------------------------------------------------------------------------------------------------------------------
//Ejercicio Número 6:
// ----------------------------------------------------------------------------------------------------------------------

//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
//        EntityManager em = emf.createEntityManager();
//        EntityTransaction tx = em.getTransaction();
//
//        try {
//            tx.begin();
//            //Creamos domicilio
//            Domicilio domicilio = new Domicilio();
//            domicilio.setCalle("Wallaby Way 42");
//            domicilio.setCiudad("Sydney");
//
//            //Crear Persona y domicilio
//            Persona persona = new Persona();
//            persona.setNombre("Dory");
//            persona.setDomicilio(domicilio);
//            em.persist(persona);
//
//            tx.commit();
//
//        } catch (Exception e) {
//            if (tx.isActive()){
//                tx.rollback();
//                System.out.println("Error al cambiar los datos");
//            }
//            e.printStackTrace();
//        }finally {
//            em.close();
//            emf.close();
//        }

//----------------------------------------------------------------------------------------------------------------------
//Ejercicio Número 7:
// ----------------------------------------------------------------------------------------------------------------------


//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("pruebaJpa_PU");
//        EntityManager em = emf.createEntityManager();
//        EntityTransaction tx = em.getTransaction();
//
//        try {
//
//            //Buscar y mostrar
//            Persona encontrada = em.find(Persona.class, 1L);
//            if (encontrada != null) {
//                System.out.println(encontrada);
//            } else  {
//                System.out.println("No se encontro el persona");
//            }
//
//        } catch (Exception e) {
//            if (tx.isActive()){
//                tx.rollback();
//                System.out.println("Error al cambiar los datos");
//            }
//            e.printStackTrace();
//        }finally {
//            em.close();
//            emf.close();
//        }


//----------------------------------------------------------------------------------------------------------------------
//Ejercicio Número 8:
// ----------------------------------------------------------------------------------------------------------------------

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
