package com.clinica.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paciente")
@SQLDelete(sql = "UPDATE paciente SET eliminado = true WHERE id = ?")
@SQLRestriction("eliminado = false")
public class Paciente extends BaseEntity {

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @Column(name = "documento", nullable = false, unique = true, length = 50)
    private String documento;

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FotoPaciente> fotos = new ArrayList<>();

    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoriaClinica> historiasClinicas = new ArrayList<>();

    public Paciente() {
    }

    public Paciente(String nombre, String apellido, String documento) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
    }

    public void addFoto(FotoPaciente foto) {
        fotos.add(foto);
        foto.setPaciente(this);
    }

    public void removeFoto(FotoPaciente foto) {
        fotos.remove(foto);
        foto.setPaciente(null);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public List<FotoPaciente> getFotos() {
        return fotos;
    }

    public void setFotos(List<FotoPaciente> fotos) {
        this.fotos = fotos;
    }

    public List<HistoriaClinica> getHistoriasClinicas() {
        return historiasClinicas;
    }

    public void setHistoriasClinicas(List<HistoriaClinica> historiasClinicas) {
        this.historiasClinicas = historiasClinicas;
    }
}
