package com.mycompany.jdbc_vs_orm.dto;

import com.mycompany.jdbc_vs_orm.model.Producto;

public class ProductoResponse {
    private Long codigo;
    private String nombre;
    private Double precio;

    public ProductoResponse() {
    }

    // Constructor de mapeo desde la entidad POJO al DTO
    public ProductoResponse(Producto producto) {
        if (producto != null) {
            this.codigo = producto.getCodigo();
            this.nombre = producto.getNombre();
            this.precio = producto.getPrecio();
        }
    }

    public ProductoResponse(Long codigo, String nombre, Double precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
    }

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