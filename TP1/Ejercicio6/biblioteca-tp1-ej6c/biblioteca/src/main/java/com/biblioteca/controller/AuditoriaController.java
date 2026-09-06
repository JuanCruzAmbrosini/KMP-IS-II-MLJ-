package com.biblioteca.controller;

import com.biblioteca.model.Autor;
import com.biblioteca.model.Editorial;
import com.biblioteca.model.Imagen;
import com.biblioteca.model.Libro;
import com.biblioteca.model.Prestamo;
import com.biblioteca.model.Usuario;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/auditoria")
public class AuditoriaController {

    private static final List<Class<?>> ENTIDADES = List.of(
            Autor.class, Editorial.class, Imagen.class, Libro.class, Prestamo.class, Usuario.class
    );

    private final EntityManager entityManager;

    public AuditoriaController(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String listar(Model model) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<RegistroAuditoria> historial = new ArrayList<>();

        for (Class<?> entidad : ENTIDADES) {
            List<?> revisiones = auditReader.createQuery()
                    .forRevisionsOfEntity(entidad, false, true)
                    .addOrder(AuditEntity.revisionNumber().desc())
                    .getResultList();

            for (Object revisionResult : revisiones) {
                Object[] revision = (Object[]) revisionResult;
                Object entidadRevision = revision[0];
                DefaultRevisionEntity revisionEntity = (DefaultRevisionEntity) revision[1];
                RevisionType tipo = (RevisionType) revision[2];
                Object id = entidadRevision == null ? "N/D"
                        : entityManager.getEntityManagerFactory().getPersistenceUnitUtil()
                        .getIdentifier(entidadRevision);
                historial.add(new RegistroAuditoria(
                        entidad.getSimpleName(), String.valueOf(id), revisionEntity.getId(),
                        tipo.name(), Instant.ofEpochMilli(revisionEntity.getTimestamp())
                ));
            }
        }

        historial.sort(Comparator.comparing(RegistroAuditoria::getFecha).reversed());
        model.addAttribute("historial", historial);
        return "auditoria/list";
    }

    @Getter
    public static class RegistroAuditoria {
        private final String entidad;
        private final String idEntidad;
        private final int revision;
        private final String operacion;
        private final Instant fecha;

        public RegistroAuditoria(String entidad, String idEntidad, int revision,
                                 String operacion, Instant fecha) {
            this.entidad = entidad;
            this.idEntidad = idEntidad;
            this.revision = revision;
            this.operacion = operacion;
            this.fecha = fecha;
        }
    }
}