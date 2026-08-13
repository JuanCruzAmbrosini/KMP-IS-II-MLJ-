package com.mycompany.jdbc_vs_orm.dao;

import com.mycompany.jdbc_vs_orm.dto.ProductoRequest;
import com.mycompany.jdbc_vs_orm.dto.ProductoResponse;
import com.mycompany.jdbc_vs_orm.model.Producto;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductoDAOImpl implements ProductoDAO {

    private final JdbcTemplate jdbcTemplate;

    public ProductoDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ProductoResponse obtenerPorCodigo(Long codigo) {
        String sql = "SELECT * FROM productos WHERE codigo = ?";
        try {
            // Mapea la fila de la BD a POJO Producto y luego lo convierte a ProductoResponse
            Producto producto = jdbcTemplate.queryForObject(
                sql, 
                new BeanPropertyRowMapper<>(Producto.class), 
                codigo
            );
            return producto != null ? new ProductoResponse(producto) : null;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<ProductoResponse> obtenerTodos() {
        String sql = "SELECT * FROM productos";
        // Mapea la lista de filas a objetos Producto
        List<Producto> productos = jdbcTemplate.query(
            sql, 
            new BeanPropertyRowMapper<>(Producto.class)
        );
        
        // Uso de Streams para mapear List<Producto> -> List<ProductoResponse>
        return productos.stream()
                .map(ProductoResponse::new)
                .toList();
    }

    @Override
    public void insertar(ProductoRequest request) {
        String sql = "INSERT INTO productos (nombre, precio) VALUES (?, ?)";
        jdbcTemplate.update(sql, request.getNombre(), request.getPrecio());
    }

    @Override
    public boolean actualizar(Long codigo, ProductoRequest request) {
        String sql = "UPDATE productos SET nombre = ?, precio = ? WHERE codigo = ?";
        int filasAfectadas = jdbcTemplate.update(
            sql, 
            request.getNombre(), 
            request.getPrecio(), 
            codigo
        );
        return filasAfectadas == 1;
    }

    @Override
    public boolean eliminar(Long codigo) {
        String sql = "DELETE FROM productos WHERE codigo = ?";
        int filasAfectadas = jdbcTemplate.update(sql, codigo);
        return filasAfectadas == 1;
    }
}