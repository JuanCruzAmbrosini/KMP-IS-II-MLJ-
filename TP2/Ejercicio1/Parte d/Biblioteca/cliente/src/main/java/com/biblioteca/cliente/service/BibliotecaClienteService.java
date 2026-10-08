package com.biblioteca.cliente.service;

import com.biblioteca.cliente.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

@Service
public class BibliotecaClienteService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${servidor.api.url:http://localhost:8080/api}")
    private String serverApiUrl;

    // ==================== PERSONAS ====================
    public List<PersonaDTO> getPersonas() {
        ResponseEntity<List<PersonaDTO>> response = restTemplate.exchange(
                serverApiUrl + "/personas",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public PersonaDTO getPersonaById(Long id) {
        return restTemplate.getForObject(serverApiUrl + "/personas/" + id, PersonaDTO.class);
    }

    public PersonaDTO createPersona(PersonaDTO dto) {
        return restTemplate.postForObject(serverApiUrl + "/personas", dto, PersonaDTO.class);
    }

    public PersonaDTO updatePersona(Long id, PersonaDTO dto) {
        HttpEntity<PersonaDTO> request = new HttpEntity<>(dto);
        ResponseEntity<PersonaDTO> response = restTemplate.exchange(
                serverApiUrl + "/personas/" + id,
                HttpMethod.PUT,
                request,
                PersonaDTO.class
        );
        return response.getBody();
    }

    public void deletePersona(Long id) {
        restTemplate.delete(serverApiUrl + "/personas/" + id);
    }

    // ==================== LIBROS ====================
    public List<LibroDTO> getLibros() {
        ResponseEntity<List<LibroDTO>> response = restTemplate.exchange(
                serverApiUrl + "/libros",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public LibroDTO getLibroById(Long id) {
        return restTemplate.getForObject(serverApiUrl + "/libros/" + id, LibroDTO.class);
    }

    public LibroDTO createLibro(LibroDTO dto) {
        return restTemplate.postForObject(serverApiUrl + "/libros", dto, LibroDTO.class);
    }

    public LibroDTO updateLibro(Long id, LibroDTO dto) {
        HttpEntity<LibroDTO> request = new HttpEntity<>(dto);
        ResponseEntity<LibroDTO> response = restTemplate.exchange(
                serverApiUrl + "/libros/" + id,
                HttpMethod.PUT,
                request,
                LibroDTO.class
        );
        return response.getBody();
    }

    public void deleteLibro(Long id) {
        restTemplate.delete(serverApiUrl + "/libros/" + id);
    }

    // ---- Libros con PDF: se reenvia al servidor como multipart (libro en JSON + archivo) ----
    public LibroDTO createLibroConPdf(LibroDTO dto, MultipartFile archivo) {
        return restTemplate.postForObject(serverApiUrl + "/libros", construirMultipart(dto, archivo), LibroDTO.class);
    }

    public LibroDTO updateLibroConPdf(Long id, LibroDTO dto, MultipartFile archivo) {
        ResponseEntity<LibroDTO> response = restTemplate.exchange(
                serverApiUrl + "/libros/" + id,
                HttpMethod.PUT,
                construirMultipart(dto, archivo),
                LibroDTO.class
        );
        return response.getBody();
    }

    public ResponseEntity<byte[]> getLibroPdf(Long id) {
        return restTemplate.getForEntity(serverApiUrl + "/libros/" + id + "/pdf", byte[].class);
    }

    private HttpEntity<MultiValueMap<String, Object>> construirMultipart(LibroDTO dto, MultipartFile archivo) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        HttpHeaders libroHeaders = new HttpHeaders();
        libroHeaders.setContentType(MediaType.APPLICATION_JSON);
        body.add("libro", new HttpEntity<>(dto, libroHeaders));

        if (archivo != null && !archivo.isEmpty()) {
            try {
                ByteArrayResource recurso = new ByteArrayResource(archivo.getBytes()) {
                    @Override
                    public String getFilename() {
                        return archivo.getOriginalFilename();
                    }
                };
                HttpHeaders archivoHeaders = new HttpHeaders();
                archivoHeaders.setContentType(MediaType.APPLICATION_PDF);
                body.add("archivo", new HttpEntity<>(recurso, archivoHeaders));
            } catch (IOException e) {
                throw new UncheckedIOException("No se pudo leer el archivo recibido", e);
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return new HttpEntity<>(body, headers);
    }

    // ==================== AUTORES ====================
    public List<AutorDTO> getAutores() {
        ResponseEntity<List<AutorDTO>> response = restTemplate.exchange(
                serverApiUrl + "/autores",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public AutorDTO getAutorById(Long id) {
        return restTemplate.getForObject(serverApiUrl + "/autores/" + id, AutorDTO.class);
    }

    public AutorDTO createAutor(AutorDTO dto) {
        return restTemplate.postForObject(serverApiUrl + "/autores", dto, AutorDTO.class);
    }

    public AutorDTO updateAutor(Long id, AutorDTO dto) {
        HttpEntity<AutorDTO> request = new HttpEntity<>(dto);
        ResponseEntity<AutorDTO> response = restTemplate.exchange(
                serverApiUrl + "/autores/" + id,
                HttpMethod.PUT,
                request,
                AutorDTO.class
        );
        return response.getBody();
    }

    public void deleteAutor(Long id) {
        restTemplate.delete(serverApiUrl + "/autores/" + id);
    }

    // ==================== LOCALIDADES ====================
    public List<LocalidadDTO> getLocalidades() {
        ResponseEntity<List<LocalidadDTO>> response = restTemplate.exchange(
                serverApiUrl + "/localidades",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public LocalidadDTO getLocalidadById(Long id) {
        return restTemplate.getForObject(serverApiUrl + "/localidades/" + id, LocalidadDTO.class);
    }

    public LocalidadDTO createLocalidad(LocalidadDTO dto) {
        return restTemplate.postForObject(serverApiUrl + "/localidades", dto, LocalidadDTO.class);
    }

    public LocalidadDTO updateLocalidad(Long id, LocalidadDTO dto) {
        HttpEntity<LocalidadDTO> request = new HttpEntity<>(dto);
        ResponseEntity<LocalidadDTO> response = restTemplate.exchange(
                serverApiUrl + "/localidades/" + id,
                HttpMethod.PUT,
                request,
                LocalidadDTO.class
        );
        return response.getBody();
    }

    public void deleteLocalidad(Long id) {
        restTemplate.delete(serverApiUrl + "/localidades/" + id);
    }

    // ==================== DOMICILIOS ====================
    public List<DomicilioDTO> getDomicilios() {
        ResponseEntity<List<DomicilioDTO>> response = restTemplate.exchange(
                serverApiUrl + "/domicilios",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public DomicilioDTO getDomicilioById(Long id) {
        return restTemplate.getForObject(serverApiUrl + "/domicilios/" + id, DomicilioDTO.class);
    }

    public DomicilioDTO createDomicilio(DomicilioDTO dto) {
        return restTemplate.postForObject(serverApiUrl + "/domicilios", dto, DomicilioDTO.class);
    }

    public DomicilioDTO updateDomicilio(Long id, DomicilioDTO dto) {
        HttpEntity<DomicilioDTO> request = new HttpEntity<>(dto);
        ResponseEntity<DomicilioDTO> response = restTemplate.exchange(
                serverApiUrl + "/domicilios/" + id,
                HttpMethod.PUT,
                request,
                DomicilioDTO.class
        );
        return response.getBody();
    }

    public void deleteDomicilio(Long id) {
        restTemplate.delete(serverApiUrl + "/domicilios/" + id);
    }
}
