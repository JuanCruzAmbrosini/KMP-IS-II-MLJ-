package com.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion.
 *
 * Arquitectura: MVC (Modelo-Vista-Controlador) + ORM (Spring Data JPA / Hibernate).
 * Capas del proyecto:
 *  - model      -> Entidades JPA (@Entity): Autor, Editorial, Imagen, Libro, Prestamo, Usuario
 *  - repository -> Interfaces Spring Data JPA (patron Repository, reemplaza al DAO manual)
 *  - service    -> Logica de negocio (capa de Servicio del patron MVC)
 *  - controller -> Clases @Controller que atienden peticiones HTTP y arman el Modelo para la Vista
 *  - security   -> Configuracion de Spring Security (autenticacion/autorizacion)
 *  - templates  -> Vistas Thymeleaf (capa de Vista)
 */
@SpringBootApplication
public class BibliotecaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BibliotecaApplication.class, args);
    }
}
