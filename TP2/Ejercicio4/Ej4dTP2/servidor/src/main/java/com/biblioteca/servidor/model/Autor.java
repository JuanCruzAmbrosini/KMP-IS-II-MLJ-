package com.biblioteca.servidor.model;

import com.biblioteca.servidor.pattern.creational.prototype.Prototype;
import jakarta.persistence.*;

/**
 * Entidad Autor: Representa los creadores literarios del sistema de biblioteca.
 * Implementa el Patrón Creacional Prototype (mediante Prototype<Autor> y Cloneable)
 * para permitir la duplicación rápida de autores y sus datos biográficos.
 */
@Entity
@Table(name = "autores")
public class Autor implements Prototype<Autor>, Cloneable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(length = 2000)
    private String biografia;

    public Autor() {
    }

    public Autor(Long id, String nombre, String apellido, String biografia) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.biografia = biografia;
    }

    // Constructor de copia para el patrón Prototype
    public Autor(Autor origen) {
        if (origen != null) {
            this.id = null; // Al clonar para persistencia nueva, se genera un ID nuevo
            this.nombre = origen.nombre;
            this.apellido = origen.apellido;
            this.biografia = origen.biografia;
        }
    }

    /**
     * Implementación del método clonar del patrón Prototype.
     * Genera una nueva instancia independiente preservando los datos biográficos.
     */
    @Override
    public Autor clonar() {
        return new Autor(this);
    }

    /**
     * Sobrescritura de clone() de Object según la convención de Java.
     */
    @Override
    public Autor clone() {
        return this.clonar();
    }

    /**
     * Clona el autor permitiendo modificar selectivamente sus atributos.
     */
    public Autor clonarConModificaciones(String nuevoNombre, String nuevoApellido, String nuevaBiografia) {
        Autor clon = this.clonar();
        if (nuevoNombre != null && !nuevoNombre.isBlank()) clon.setNombre(nuevoNombre);
        if (nuevoApellido != null && !nuevoApellido.isBlank()) clon.setApellido(nuevoApellido);
        if (nuevaBiografia != null && !nuevaBiografia.isBlank()) clon.setBiografia(nuevaBiografia);
        return clon;
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }
}
