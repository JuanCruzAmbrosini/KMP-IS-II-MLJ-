package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Autor;
import com.biblioteca.servidor.repository.AutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class AutorService extends BaseService<Autor, Long> {

    @Autowired
    private AutorRepository autorRepository;

    @Override
    protected JpaRepository<Autor, Long> getRepository() {
        return autorRepository;
    }

    @Override
    protected String getEntityName() {
        return "Autor";
    }

    @Override
    protected void actualizarDatos(Autor existente, Autor nuevo) {
        existente.setNombre(nuevo.getNombre());
        existente.setApellido(nuevo.getApellido());
        existente.setBiografia(nuevo.getBiografia());
    }

    /**
     * Patrón Creacional Prototype:
     * Clona un autor existente mediante su método clonar() / clonarConModificaciones()
     * y persiste la nueva entidad clonada.
     */
    public Autor clonarAutor(Long autorId, String nuevoNombre, String nuevoApellido, String nuevaBiografia) {
        Autor original = findById(autorId)
                .orElseThrow(() -> new RuntimeException("Autor no encontrado con id: " + autorId));

        Autor clon = original.clonarConModificaciones(nuevoNombre, nuevoApellido, nuevaBiografia);
        return save(clon);
    }
}
