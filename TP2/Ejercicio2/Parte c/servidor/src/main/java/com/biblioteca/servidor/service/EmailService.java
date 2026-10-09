package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.EmailLog;
import com.biblioteca.servidor.model.Libro;
import com.biblioteca.servidor.model.Persona;

import java.util.List;

public interface EmailService {
    EmailLog enviarRecordatorioVencimiento(Persona persona, Libro libro);
    EmailLog enviarSalutacionCumpleanios(Persona persona);
    List<EmailLog> obtenerHistorial();
}
