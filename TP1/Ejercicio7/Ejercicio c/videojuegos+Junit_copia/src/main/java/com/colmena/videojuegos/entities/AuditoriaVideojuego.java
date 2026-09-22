package com.colmena.videojuegos.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_videojuegos")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaVideojuego {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long videojuegoId;
    private String tituloVideojuego;
    private String accion;
    private String campo;
    private String valorAnterior;
    private String valorNuevo;
    private LocalDateTime fecha;
}