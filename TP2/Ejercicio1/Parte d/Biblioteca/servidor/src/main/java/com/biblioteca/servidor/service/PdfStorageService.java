package com.biblioteca.servidor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.Normalizer;
import java.util.Arrays;

/**
 * Responsable de guardar, leer y borrar los PDF de los libros en el disco del servidor.
 * Carpeta por defecto: C:/biblioteca (configurable con biblioteca.pdf.directorio).
 * Formato del archivo: libro_<nombre del libro>.pdf
 */
@Service
public class PdfStorageService {

    private static final Logger log = LoggerFactory.getLogger(PdfStorageService.class);
    private static final byte[] FIRMA_PDF = "%PDF-".getBytes(StandardCharsets.US_ASCII);

    private final Path directorio;

    public PdfStorageService(@Value("${biblioteca.pdf.directorio:C:/biblioteca}") String directorio) {
        this.directorio = Paths.get(directorio).toAbsolutePath().normalize();
    }

    /** Arma el nombre de archivo a partir del titulo: "Cien Años de Soledad" -> libro_Cien_Anos_de_Soledad.pdf */
    public String nombreArchivo(String titulo) {
        String base = Normalizer.normalize(titulo == null ? "" : titulo, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")            // saca tildes
                .replaceAll("[^A-Za-z0-9]+", "_")     // solo letras/numeros: evita caracteres invalidos y path traversal
                .replaceAll("^_+|_+$", "");
        if (base.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El titulo del libro no permite generar un nombre de archivo valido");
        }
        return "libro_" + base + ".pdf";
    }

    public void guardar(String nombreArchivo, MultipartFile archivo) {
        validarPdf(archivo);
        Path destino = resolver(nombreArchivo);
        try (InputStream in = archivo.getInputStream()) {
            Files.createDirectories(directorio);
            Files.copy(in, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el PDF en " + destino, e);
        }
    }

    public Resource cargar(String nombreArchivo) {
        Path ruta = resolver(nombreArchivo);
        if (!Files.isReadable(ruta)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El archivo PDF no se encuentra en el servidor");
        }
        return new FileSystemResource(ruta);
    }

    public void eliminar(String nombreArchivo) {
        try {
            Files.deleteIfExists(resolver(nombreArchivo));
        } catch (IOException e) {
            log.warn("No se pudo eliminar el PDF {}: {}", nombreArchivo, e.getMessage());
        }
    }

    private Path resolver(String nombreArchivo) {
        Path ruta = directorio.resolve(nombreArchivo).normalize();
        if (!ruta.startsWith(directorio)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de archivo invalido");
        }
        return ruta;
    }

    private void validarPdf(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo PDF esta vacio");
        }
        try (InputStream in = archivo.getInputStream()) {
            byte[] cabecera = in.readNBytes(FIRMA_PDF.length);
            if (!Arrays.equals(cabecera, FIRMA_PDF)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El archivo no es un PDF valido");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo recibido", e);
        }
    }
}
