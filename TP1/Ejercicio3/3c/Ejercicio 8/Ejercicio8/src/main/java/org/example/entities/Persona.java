package org.example.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NamedQuery(
        name = "Persona.buscarPorCiudad",
        query = "SELECT p FROM Persona p JOIN p.domicilio d WHERE d.ciudad = :ciudad"
)
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "persona_curso",
            joinColumns = @JoinColumn(name = "Persona_id"), //FK a persona
            inverseJoinColumns = @JoinColumn(name = "curso_id") //FK a curso
    )
    private Set<Curso> cursos = new HashSet<>();
    @OneToOne(cascade = CascadeType.ALL)
    private Domicilio domicilio;

    public Persona() {}

    public Persona(String nombre) {
        this.nombre = nombre;
    }

    public void agregarCurso(Curso curso) {
        this.cursos.add(curso);
        curso.agregarInscripto(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Set<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(Set<Curso> cursos) {
        this.cursos = cursos;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    @Override
    public String toString() {
        return "Persona{" +
                "domicilio=" + domicilio +
                ", id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}