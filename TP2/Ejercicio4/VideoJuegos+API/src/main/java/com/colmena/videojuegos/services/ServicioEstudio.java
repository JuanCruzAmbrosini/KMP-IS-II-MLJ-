package com.colmena.videojuegos.services;

import com.colmena.videojuegos.entities.Estudio;
import com.colmena.videojuegos.repositories.RepositorioEstudio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ServicioEstudio extends BaseService<Estudio, Long> {

    @Autowired
    private RepositorioEstudio repositorio;

    @Override
    protected JpaRepository<Estudio, Long> getRepository() {
        return repositorio;
    }

    @Override
    protected String getEntityName() {
        return "Estudio";
    }

    @Override
    protected void actualizarDatos(Estudio existente, Estudio nuevo) {
        existente.setNombre(nuevo.getNombre());
        existente.setActivo(nuevo.isActivo());
    }
}
