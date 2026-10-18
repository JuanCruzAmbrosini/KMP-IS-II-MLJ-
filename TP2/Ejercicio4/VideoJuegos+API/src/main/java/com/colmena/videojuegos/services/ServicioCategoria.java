package com.colmena.videojuegos.services;

import com.colmena.videojuegos.entities.Categoria;
import com.colmena.videojuegos.repositories.RepositorioCategoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ServicioCategoria extends BaseService<Categoria, Long> {

    @Autowired
    private RepositorioCategoria repositorio;

    @Override
    protected JpaRepository<Categoria, Long> getRepository() {
        return repositorio;
    }

    @Override
    protected String getEntityName() {
        return "Categoría";
    }

    @Override
    protected void actualizarDatos(Categoria existente, Categoria nuevo) {
        existente.setNombre(nuevo.getNombre());
        existente.setActivo(nuevo.isActivo());
    }
}
