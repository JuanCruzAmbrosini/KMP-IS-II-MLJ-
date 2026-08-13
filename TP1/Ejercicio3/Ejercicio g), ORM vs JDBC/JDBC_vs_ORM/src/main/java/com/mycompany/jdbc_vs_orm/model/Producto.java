package com.mycompany.jdbc_vs_orm.model;

public class Producto {
    private Long codigo;
    private String nombre;
    private Double precio;

    // Constructor vacío (necesario para RowMapper)
    public Producto() {
    }

    // Constructor completo
    public Producto(Long codigo, String nombre, Double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
    }

    // Getters y Setters
    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}