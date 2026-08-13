package com.mycompany.jdbc_vs_orm.dto;

public class ProductoRequest {
    private String nombre;
    private Double precio;

    public ProductoRequest() {
    }

    public ProductoRequest(String nombre, Double precio) {
        this.nombre = nombre;
        this.precio = precio;
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