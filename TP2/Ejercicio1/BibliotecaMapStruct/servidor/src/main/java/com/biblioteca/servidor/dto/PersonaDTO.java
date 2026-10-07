package com.biblioteca.servidor.dto;

import java.util.ArrayList;
import java.util.List;

public class PersonaDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private int dni;
    private DomicilioDTO domicilio;
    private List<LibroDTO> libros = new ArrayList<>();

    public PersonaDTO() {
    }

    public PersonaDTO(Long id, String nombre, String apellido, int dni, DomicilioDTO domicilio, List<LibroDTO> libros) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.domicilio = domicilio;
        this.libros = libros != null ? libros : new ArrayList<>();
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

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public DomicilioDTO getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(DomicilioDTO domicilio) {
        this.domicilio = domicilio;
    }

    public List<LibroDTO> getLibros() {
        return libros;
    }

    public void setLibros(List<LibroDTO> libros) {
        this.libros = libros;
    }
}

