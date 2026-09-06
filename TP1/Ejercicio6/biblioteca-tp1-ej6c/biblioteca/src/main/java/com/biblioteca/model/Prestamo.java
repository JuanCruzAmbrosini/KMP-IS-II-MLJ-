package com.biblioteca.model;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Prestamo de un ejemplar de Libro a un Usuario.
 * Relaciones: Libro 1..1 -> Prestamo 1..*   /   Usuario 1..1 -> Prestamo 1..*
 */
@Entity
@Audited
@Table(name = "prestamo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prestamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de prestamo es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaPrestamo;

    private LocalDate fechaDevolucion;

    @Column(nullable = false)
    private boolean alta = true;

    @ManyToOne(optional = false)
    @JoinColumn(name = "libro_id", nullable = false)
    private Libro libro;

    @ManyToOne(optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
