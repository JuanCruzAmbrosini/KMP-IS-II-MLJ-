package com.biblioteca.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Imagen de tapa de un libro. Relacion uno a uno con Libro (1..1 <-> 1..1).
 */
@Entity
@Table(name = "imagen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Imagen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 100)
    private String mime;

    @Lob
    @Column(name = "contenido")
    private byte[] contenido;

    @OneToOne(mappedBy = "imagen")
    private Libro libro;
}
