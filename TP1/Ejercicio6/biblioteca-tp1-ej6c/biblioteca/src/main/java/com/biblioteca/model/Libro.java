package com.biblioteca.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Libro. Nodo central del modelo:
 *  - Autor      1..* <-> 1..*  (muchos a muchos)
 *  - Editorial  1..* <-> 1..*  (muchos a muchos)
 *  - Imagen     1..1 <-> 1..1  (uno a uno, Libro es el lado dueño de la relacion)
 *  - Prestamo   1..1 <-> 1..*  (un libro puede tener muchos prestamos historicos)
 */
@Entity
@Audited
@Table(name = "libro")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El ISBN es obligatorio")
    @Column(nullable = false, unique = true)
    private Long isbn;

    @NotBlank(message = "El titulo es obligatorio")
    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(name = "anio")
    private int anio;

    @PositiveOrZero
    private int ejemplares;

    @PositiveOrZero
    private int ejemplaresPrestados;

    @PositiveOrZero
    private int ejemplaresRestantes;

    @Column(nullable = false)
    private boolean alta = true;

    @ManyToMany
    @JoinTable(
            name = "libro_autor",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name = "autor_id")
    )
    private Set<Autor> autores = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "libro_editorial",
            joinColumns = @JoinColumn(name = "libro_id"),
            inverseJoinColumns = @JoinColumn(name = "editorial_id")
    )
    private Set<Editorial> editoriales = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "imagen_id", referencedColumnName = "id")
    private Imagen imagen;

    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Prestamo> prestamos = new HashSet<>();
}
