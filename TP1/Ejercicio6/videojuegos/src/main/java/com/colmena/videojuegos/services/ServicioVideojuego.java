package com.colmena.videojuegos.services;

import com.colmena.videojuegos.entities.AuditoriaVideojuego;
import com.colmena.videojuegos.entities.Videojuego;
import com.colmena.videojuegos.repositories.RepositorioAuditoriaVideojuego;
import com.colmena.videojuegos.repositories.RepositorioVideojuego;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class ServicioVideojuego implements ServicioBase<Videojuego>{
    @Autowired
    private RepositorioVideojuego repositorio;

    @Autowired
    private RepositorioAuditoriaVideojuego repositorioAuditoria;

    @Override
    @Transactional
    public List<Videojuego> findAll() throws Exception {
        try {
            List<Videojuego> entities = this.repositorio.findAll();
            return entities;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Videojuego findById(long id) throws Exception {
        try {
            Optional<Videojuego> opt = this.repositorio.findById(id);
            return opt.get();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Videojuego saveOne(Videojuego entity) throws Exception {
        try {
            Videojuego videojuego = this.repositorio.save(entity);
            registrarAlta(videojuego);
            return videojuego;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public Videojuego updateOne(Videojuego entity, long id) throws Exception {
        try {
            Optional<Videojuego> opt = this.repositorio.findById(id);
            Videojuego anterior = opt.get();
            registrarCambios(anterior, entity, id);
            Videojuego videojuego = this.repositorio.save(entity);
            return videojuego;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deleteById(long id) throws Exception {
        try {
            Optional<Videojuego> opt = this.repositorio.findById(id);
            if (!opt.isEmpty()) {
                Videojuego videojuego = opt.get();
                boolean estabaActivo = videojuego.isActivo();
                videojuego.setActivo(!videojuego.isActivo());
                this.repositorio.save(videojuego);
                registrarCambio(videojuego, estabaActivo ? "ELIMINAR" : "REACTIVAR", "activo", estabaActivo, videojuego.isActivo());
            } else {
                throw new Exception();
            }
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    /*   Metodos nuevos   */

    @Transactional
    public List<Videojuego> findAllByActivo() throws Exception{
        try {
            List<Videojuego> entities = this.repositorio.findAllByActivo();
            return entities;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public Videojuego findByIdAndActivo(long id) throws Exception {
        try {
            Optional<Videojuego> opt = this.repositorio.findByIdAndActivo(id);
            return opt.get();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Transactional
    public List<Videojuego> findByTitle(String q) throws Exception{
        try{
            List<Videojuego> entities = this.repositorio.findByTitle(q);
            return entities;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private void registrarAlta(Videojuego videojuego) {
        registrarCambio(videojuego, "CREAR", "titulo", null, videojuego.getTitulo());
        registrarCambio(videojuego, "CREAR", "descripcion", null, videojuego.getDescripcion());
        registrarCambio(videojuego, "CREAR", "imagen", null, videojuego.getImagen());
        registrarCambio(videojuego, "CREAR", "precio", null, videojuego.getPrecio());
        registrarCambio(videojuego, "CREAR", "stock", null, videojuego.getStock());
        registrarCambio(videojuego, "CREAR", "fechaLanzamiento", null, videojuego.getFechaLanzamiento());
        registrarCambio(videojuego, "CREAR", "categoria", null, nombreCategoria(videojuego));
        registrarCambio(videojuego, "CREAR", "estudio", null, nombreEstudio(videojuego));
        registrarCambio(videojuego, "CREAR", "activo", null, videojuego.isActivo());
    }

    private void registrarCambios(Videojuego anterior, Videojuego actual, long id) {
        registrarSiCambio(actual, id, "titulo", anterior.getTitulo(), actual.getTitulo());
        registrarSiCambio(actual, id, "descripcion", anterior.getDescripcion(), actual.getDescripcion());
        registrarSiCambio(actual, id, "imagen", anterior.getImagen(), actual.getImagen());
        registrarSiCambio(actual, id, "precio", anterior.getPrecio(), actual.getPrecio());
        registrarSiCambio(actual, id, "stock", anterior.getStock(), actual.getStock());
        registrarSiCambio(actual, id, "fechaLanzamiento", anterior.getFechaLanzamiento(), actual.getFechaLanzamiento());
        registrarSiCambio(actual, id, "categoria", nombreCategoria(anterior), nombreCategoria(actual));
        registrarSiCambio(actual, id, "estudio", nombreEstudio(anterior), nombreEstudio(actual));
        registrarSiCambio(actual, id, "activo", anterior.isActivo(), actual.isActivo());
    }

    private void registrarSiCambio(Videojuego videojuego, long id, String campo, Object anterior, Object actual) {
        if (anterior == null ? actual != null : !anterior.equals(actual)) {
            registrarCambio(videojuego, "MODIFICAR", campo, anterior, actual, id);
        }
    }

    private void registrarCambio(Videojuego videojuego, String accion, String campo, Object anterior, Object nuevo) {
        registrarCambio(videojuego, accion, campo, anterior, nuevo, videojuego.getId());
    }

    private void registrarCambio(Videojuego videojuego, String accion, String campo, Object anterior, Object nuevo, long id) {
        AuditoriaVideojuego auditoria = new AuditoriaVideojuego(
                0,
                id,
                videojuego.getTitulo(),
                accion,
                campo,
                convertir(anterior),
                convertir(nuevo),
                LocalDateTime.now());
        this.repositorioAuditoria.save(auditoria);
    }

    private String convertir(Object valor) {
        return valor == null ? "-" : valor.toString();
    }

    private String nombreCategoria(Videojuego videojuego) {
        return videojuego.getCategoria() == null ? null : videojuego.getCategoria().getNombre();
    }

    private String nombreEstudio(Videojuego videojuego) {
        return videojuego.getEstudio() == null ? null : videojuego.getEstudio().getNombre();
    }
}
