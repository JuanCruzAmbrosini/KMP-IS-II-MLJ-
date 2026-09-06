package com.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Editorial que publica libros. Relacion muchos a muchos con Libro (1..* <-> 1..*).
 */
@Entity
@Table(name = "editorial")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Editorial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la editorial es obligatorio")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false)
    private boolean alta = true;

    @ManyToMany(mappedBy = "editoriales")
    @JsonIgnore
    private Set<Libro> libros = new HashSet<>();
}
