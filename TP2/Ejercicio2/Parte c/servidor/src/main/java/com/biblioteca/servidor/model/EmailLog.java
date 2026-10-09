package com.biblioteca.servidor.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs_emails")
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tipo; // RECORDATORIO_VENCIMIENTO | SALUTACION_CUMPLEANIOS

    @Column(nullable = false)
    private String destinatario;

    private String nombreDestinatario;

    @Column(nullable = false)
    private String asunto;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String cuerpoHtml;

    @Column(nullable = false)
    private LocalDateTime fechaEnvio;

    @Column(nullable = false)
    private String estado; // ENVIADO_SIMULADO | ENVIADO_SMTP | ERROR_SMTP

    public EmailLog() {
    }

    public EmailLog(Long id, String tipo, String destinatario, String nombreDestinatario, String asunto, String cuerpoHtml, LocalDateTime fechaEnvio, String estado) {
        this.id = id;
        this.tipo = tipo;
        this.destinatario = destinatario;
        this.nombreDestinatario = nombreDestinatario;
        this.asunto = asunto;
        this.cuerpoHtml = cuerpoHtml;
        this.fechaEnvio = fechaEnvio;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getNombreDestinatario() {
        return nombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getCuerpoHtml() {
        return cuerpoHtml;
    }

    public void setCuerpoHtml(String cuerpoHtml) {
        this.cuerpoHtml = cuerpoHtml;
    }

    public LocalDateTime getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(LocalDateTime fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
