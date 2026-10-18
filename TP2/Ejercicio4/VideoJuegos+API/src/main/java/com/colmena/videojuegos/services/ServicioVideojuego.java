package com.colmena.videojuegos.services;

import com.colmena.videojuegos.entities.Videojuego;
import com.colmena.videojuegos.repositories.RepositorioCategoria;
import com.colmena.videojuegos.repositories.RepositorioEstudio;
import com.colmena.videojuegos.repositories.RepositorioVideojuego;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ServicioVideojuego extends BaseService<Videojuego, Long> {

    @Autowired
    private RepositorioVideojuego repositorio;

    @Autowired
    private RepositorioCategoria repositorioCategoria;

    @Autowired
    private RepositorioEstudio repositorioEstudio;

    @Override
    protected JpaRepository<Videojuego, Long> getRepository() {
        return repositorio;
    }

    @Override
    protected String getEntityName() {
        return "Videojuego";
    }

    @Override
    protected void validar(Videojuego entity) {
        if (entity.getTitulo() != null && entity.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título del videojuego no puede estar vacío");
        }
    }

    @Override
    protected void preSave(Videojuego entity) {
        vincularRelaciones(entity);
    }

    @Override
    protected void preUpdate(Long id, Videojuego entity) {
        vincularRelaciones(entity);
    }

    private void vincularRelaciones(Videojuego entity) {
        if (entity.getCategoria() != null && entity.getCategoria().getId() > 0) {
            repositorioCategoria.findById(entity.getCategoria().getId())
                    .ifPresent(entity::setCategoria);
        }
        if (entity.getEstudio() != null && entity.getEstudio().getId() > 0) {
            repositorioEstudio.findById(entity.getEstudio().getId())
                    .ifPresent(entity::setEstudio);
        }
    }

    @Override
    protected void actualizarDatos(Videojuego existente, Videojuego nuevo) {
        existente.setTitulo(nuevo.getTitulo());
        existente.setDescripcion(nuevo.getDescripcion());
        existente.setPrecio(nuevo.getPrecio());
        existente.setStock(nuevo.getStock());
        existente.setFechaLanzamiento(nuevo.getFechaLanzamiento());
        existente.setActivo(nuevo.isActivo());
        if (nuevo.getImagen() != null && !nuevo.getImagen().isEmpty()) {
            existente.setImagen(nuevo.getImagen());
        }
        if (nuevo.getCategoria() != null) {
            existente.setCategoria(nuevo.getCategoria());
        }
        if (nuevo.getEstudio() != null) {
            existente.setEstudio(nuevo.getEstudio());
        }
    }

    @Override
    protected void ejecutarEliminacion(Long id) {
        Optional<Videojuego> opt = repositorio.findById(id);
        if (opt.isPresent()) {
            Videojuego videojuego = opt.get();
            videojuego.setActivo(!videojuego.isActivo());
            repositorio.save(videojuego);
        } else {
            throw new RuntimeException("No existe el videojuego con id " + id);
        }
    }

    // --- Métodos de dominio específicos ---

    @Transactional(readOnly = true)
    public List<Videojuego> findAllByActivo() {
        return repositorio.findAllByActivo();
    }

    @Transactional(readOnly = true)
    public Videojuego findByIdAndActivo(long id) {
        return repositorio.findByIdAndActivo(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Videojuego> findByTitle(String q) {
        return repositorio.findByTitle(q);
    }
}
