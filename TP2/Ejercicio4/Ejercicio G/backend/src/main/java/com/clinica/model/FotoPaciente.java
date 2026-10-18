package com.clinica.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.Basic;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "foto_paciente")
@SQLDelete(sql = "UPDATE foto_paciente SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado = false")
public class FotoPaciente extends BaseEntity {

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "mime", nullable = false, length = 100)
    private String mime;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "contenido", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] contenido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    @JsonIgnore
    private Paciente paciente;

    public FotoPaciente() {
    }

    public FotoPaciente(String nombre, String mime, byte[] contenido, Paciente paciente) {
        this.nombre = nombre;
        this.mime = mime;
        this.contenido = contenido;
        this.paciente = paciente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMime() {
        return mime;
    }

    public void setMime(String mime) {
        this.mime = mime;
    }

    public byte[] getContenido() {
        return contenido;
    }

    public void setContenido(byte[] contenido) {
        this.contenido = contenido;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }
}
