package com.mycompany.jdbc_vs_orm.dao;

import com.mycompany.jdbc_vs_orm.dto.ProductoRequest;
import com.mycompany.jdbc_vs_orm.dto.ProductoResponse;
import java.util.List;

public interface ProductoDAO {
    ProductoResponse obtenerPorCodigo(Long codigo);
    List<ProductoResponse> obtenerTodos();
    void insertar(ProductoRequest request);
    boolean actualizar(Long codigo, ProductoRequest request);
    boolean eliminar(Long codigo);
}