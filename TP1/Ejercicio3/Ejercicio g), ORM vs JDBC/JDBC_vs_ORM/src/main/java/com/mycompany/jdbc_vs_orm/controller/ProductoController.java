package com.mycompany.jdbc_vs_orm.controller;

import com.mycompany.jdbc_vs_orm.dao.ProductoDAO;
import com.mycompany.jdbc_vs_orm.dto.ProductoRequest;
import com.mycompany.jdbc_vs_orm.dto.ProductoResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoDAO productoDAO;

    public ProductoController(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    @GetMapping
    public List<ProductoResponse> listarTodos() {
        return productoDAO.obtenerTodos();
    }

    @GetMapping("/{codigo}")
    public ProductoResponse obtenerPorCodigo(@PathVariable Long codigo) {
        return productoDAO.obtenerPorCodigo(codigo);
    }

    @PostMapping
    public String crear(@RequestBody ProductoRequest request) {
        productoDAO.insertar(request);
        return "Producto creado exitosamente";
    }

    @PutMapping("/{codigo}")
    public String actualizar(@PathVariable Long codigo, @RequestBody ProductoRequest request) {
        boolean modificado = productoDAO.actualizar(codigo, request);
        return modificado ? "Producto actualizado" : "Producto no encontrado";
    }

    @DeleteMapping("/{codigo}")
    public String eliminar(@PathVariable Long codigo) {
        boolean eliminado = productoDAO.eliminar(codigo);
        return eliminado ? "Producto eliminado" : "Producto no encontrado";
    }
}