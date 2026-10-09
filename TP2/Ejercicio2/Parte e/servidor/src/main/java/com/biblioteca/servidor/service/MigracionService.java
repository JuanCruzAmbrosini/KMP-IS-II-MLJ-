package com.biblioteca.servidor.service;

import com.biblioteca.servidor.model.Domicilio;
import com.biblioteca.servidor.model.Persona;
import com.biblioteca.servidor.repository.DomicilioRepository;
import com.biblioteca.servidor.repository.PersonaRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
public class MigracionService {

    private static final String FILE_NAME = "migración.txt";

    private final PersonaRepository personaRepository;
    private final DomicilioRepository domicilioRepository;

    public MigracionService(PersonaRepository personaRepository, DomicilioRepository domicilioRepository) {
        this.personaRepository = personaRepository;
        this.domicilioRepository = domicilioRepository;
    }

    @Transactional
    public MigrationResult importar() throws IOException {
        List<String> errores = new ArrayList<>();
        int importados = 0;
        int omitidos = 0;

        try (BufferedReader reader = abrirArchivo()) {
            String line;
            int numeroLinea = 0;
            while ((line = reader.readLine()) != null) {
                numeroLinea++;
                if (line.isBlank()) {
                    continue;
                }

                try {
                    StringTokenizer tokens = new StringTokenizer(line, ";");
                    if (tokens.countTokens() != 5) {
                        throw new IllegalArgumentException("se esperaban 5 campos separados por ';'");
                    }

                    String nombre = tokens.nextToken().trim();
                    String apellido = tokens.nextToken().trim();
                    int dni = Integer.parseInt(tokens.nextToken().trim());
                    String calle = tokens.nextToken().trim();
                    int numero = Integer.parseInt(tokens.nextToken().trim());

                    if (nombre.isBlank() || apellido.isBlank() || calle.isBlank()) {
                        throw new IllegalArgumentException("nombre, apellido y calle no pueden estar vacíos");
                    }
                    if (personaRepository.existsByDni(dni)) {
                        omitidos++;
                        errores.add("Línea " + numeroLinea + ": DNI " + dni + " ya existe; se omitió");
                        continue;
                    }

                    Domicilio domicilio = domicilioRepository.save(new Domicilio(null, calle, numero, null));
                    personaRepository.save(new Persona(null, nombre, apellido, dni, domicilio, new ArrayList<>()));
                    importados++;
                } catch (IllegalArgumentException e) {
                    errores.add("Línea " + numeroLinea + ": " + e.getMessage());
                }
            }
        }

        return new MigrationResult(FILE_NAME, importados, omitidos, errores);
    }

    private BufferedReader abrirArchivo() throws IOException {
        Path archivoExterno = Path.of(FILE_NAME);
        if (Files.exists(archivoExterno)) {
            return Files.newBufferedReader(archivoExterno, StandardCharsets.UTF_8);
        }

        InputStream recurso = new ClassPathResource(FILE_NAME).getInputStream();
        return new BufferedReader(new InputStreamReader(recurso, StandardCharsets.UTF_8));
    }

    public record MigrationResult(String archivo, int importados, int omitidos, List<String> errores) {
    }
}
