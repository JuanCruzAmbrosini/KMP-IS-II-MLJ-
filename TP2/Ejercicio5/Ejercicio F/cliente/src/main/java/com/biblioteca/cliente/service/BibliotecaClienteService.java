package com.biblioteca.cliente.service;

import com.biblioteca.cliente.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class BibliotecaClienteService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired(required = false)
    private HttpServletRequest httpServletRequest;

    @Value("${servidor.api.url:http://localhost:8080/api}")
    private String serverApiUrl;

    /**
     * Construye un HttpEntity inyectando el header Authorization si viene presente en la petición entrante.
     */
    private <T> HttpEntity<T> createAuthEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (httpServletRequest != null) {
            String authHeader = httpServletRequest.getHeader("Authorization");
            if (StringUtils.hasText(authHeader)) {
                headers.set("Authorization", authHeader);
            }
        }
        return new HttpEntity<>(body, headers);
    }

    // ==================== AUTENTICACIÓN ====================
    public Object login(Map<String, String> credentials) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(credentials, headers);
        ResponseEntity<Object> response = restTemplate.postForEntity(
                serverApiUrl + "/auth/login",
                request,
                Object.class
        );
        return response.getBody();
    }

    public Object register(Map<String, Object> userData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userData, headers);
        ResponseEntity<Object> response = restTemplate.postForEntity(
                serverApiUrl + "/auth/register",
                request,
                Object.class
        );
        return response.getBody();
    }

    public Object me() {
        ResponseEntity<Object> response = restTemplate.exchange(
                serverApiUrl + "/auth/me",
                HttpMethod.GET,
                createAuthEntity(null),
                Object.class
        );
        return response.getBody();
    }

    // ==================== PERSONAS ====================
    public List<PersonaDTO> getPersonas() {
        ResponseEntity<List<PersonaDTO>> response = restTemplate.exchange(
                serverApiUrl + "/personas",
                HttpMethod.GET,
                createAuthEntity(null),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public PersonaDTO getPersonaById(Long id) {
        ResponseEntity<PersonaDTO> response = restTemplate.exchange(
                serverApiUrl + "/personas/" + id,
                HttpMethod.GET,
                createAuthEntity(null),
                PersonaDTO.class
        );
        return response.getBody();
    }

    public PersonaDTO createPersona(PersonaDTO dto) {
        ResponseEntity<PersonaDTO> response = restTemplate.exchange(
                serverApiUrl + "/personas",
                HttpMethod.POST,
                createAuthEntity(dto),
                PersonaDTO.class
        );
        return response.getBody();
    }

    public PersonaDTO updatePersona(Long id, PersonaDTO dto) {
        ResponseEntity<PersonaDTO> response = restTemplate.exchange(
                serverApiUrl + "/personas/" + id,
                HttpMethod.PUT,
                createAuthEntity(dto),
                PersonaDTO.class
        );
        return response.getBody();
    }

    public void deletePersona(Long id) {
        restTemplate.exchange(
                serverApiUrl + "/personas/" + id,
                HttpMethod.DELETE,
                createAuthEntity(null),
                Void.class
        );
    }

    // ==================== LIBROS ====================
    public List<LibroDTO> getLibros() {
        ResponseEntity<List<LibroDTO>> response = restTemplate.exchange(
                serverApiUrl + "/libros",
                HttpMethod.GET,
                createAuthEntity(null),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public LibroDTO getLibroById(Long id) {
        ResponseEntity<LibroDTO> response = restTemplate.exchange(
                serverApiUrl + "/libros/" + id,
                HttpMethod.GET,
                createAuthEntity(null),
                LibroDTO.class
        );
        return response.getBody();
    }

    public LibroDTO createLibro(LibroDTO dto) {
        ResponseEntity<LibroDTO> response = restTemplate.exchange(
                serverApiUrl + "/libros",
                HttpMethod.POST,
                createAuthEntity(dto),
                LibroDTO.class
        );
        return response.getBody();
    }

    public LibroDTO updateLibro(Long id, LibroDTO dto) {
        ResponseEntity<LibroDTO> response = restTemplate.exchange(
                serverApiUrl + "/libros/" + id,
                HttpMethod.PUT,
                createAuthEntity(dto),
                LibroDTO.class
        );
        return response.getBody();
    }

    public void deleteLibro(Long id) {
        restTemplate.exchange(
                serverApiUrl + "/libros/" + id,
                HttpMethod.DELETE,
                createAuthEntity(null),
                Void.class
        );
    }

    // ==================== AUTORES ====================
    public List<AutorDTO> getAutores() {
        ResponseEntity<List<AutorDTO>> response = restTemplate.exchange(
                serverApiUrl + "/autores",
                HttpMethod.GET,
                createAuthEntity(null),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public AutorDTO getAutorById(Long id) {
        ResponseEntity<AutorDTO> response = restTemplate.exchange(
                serverApiUrl + "/autores/" + id,
                HttpMethod.GET,
                createAuthEntity(null),
                AutorDTO.class
        );
        return response.getBody();
    }

    public AutorDTO createAutor(AutorDTO dto) {
        ResponseEntity<AutorDTO> response = restTemplate.exchange(
                serverApiUrl + "/autores",
                HttpMethod.POST,
                createAuthEntity(dto),
                AutorDTO.class
        );
        return response.getBody();
    }

    public AutorDTO updateAutor(Long id, AutorDTO dto) {
        ResponseEntity<AutorDTO> response = restTemplate.exchange(
                serverApiUrl + "/autores/" + id,
                HttpMethod.PUT,
                createAuthEntity(dto),
                AutorDTO.class
        );
        return response.getBody();
    }

    public void deleteAutor(Long id) {
        restTemplate.exchange(
                serverApiUrl + "/autores/" + id,
                HttpMethod.DELETE,
                createAuthEntity(null),
                Void.class
        );
    }

    // ==================== LOCALIDADES ====================
    public List<LocalidadDTO> getLocalidades() {
        ResponseEntity<List<LocalidadDTO>> response = restTemplate.exchange(
                serverApiUrl + "/localidades",
                HttpMethod.GET,
                createAuthEntity(null),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public LocalidadDTO getLocalidadById(Long id) {
        ResponseEntity<LocalidadDTO> response = restTemplate.exchange(
                serverApiUrl + "/localidades/" + id,
                HttpMethod.GET,
                createAuthEntity(null),
                LocalidadDTO.class
        );
        return response.getBody();
    }

    public LocalidadDTO createLocalidad(LocalidadDTO dto) {
        ResponseEntity<LocalidadDTO> response = restTemplate.exchange(
                serverApiUrl + "/localidades",
                HttpMethod.POST,
                createAuthEntity(dto),
                LocalidadDTO.class
        );
        return response.getBody();
    }

    public LocalidadDTO updateLocalidad(Long id, LocalidadDTO dto) {
        ResponseEntity<LocalidadDTO> response = restTemplate.exchange(
                serverApiUrl + "/localidades/" + id,
                HttpMethod.PUT,
                createAuthEntity(dto),
                LocalidadDTO.class
        );
        return response.getBody();
    }

    public void deleteLocalidad(Long id) {
        restTemplate.exchange(
                serverApiUrl + "/localidades/" + id,
                HttpMethod.DELETE,
                createAuthEntity(null),
                Void.class
        );
    }

    // ==================== DOMICILIOS ====================
    public List<DomicilioDTO> getDomicilios() {
        ResponseEntity<List<DomicilioDTO>> response = restTemplate.exchange(
                serverApiUrl + "/domicilios",
                HttpMethod.GET,
                createAuthEntity(null),
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public DomicilioDTO getDomicilioById(Long id) {
        ResponseEntity<DomicilioDTO> response = restTemplate.exchange(
                serverApiUrl + "/domicilios/" + id,
                HttpMethod.GET,
                createAuthEntity(null),
                DomicilioDTO.class
        );
        return response.getBody();
    }

    public DomicilioDTO createDomicilio(DomicilioDTO dto) {
        ResponseEntity<DomicilioDTO> response = restTemplate.exchange(
                serverApiUrl + "/domicilios",
                HttpMethod.POST,
                createAuthEntity(dto),
                DomicilioDTO.class
        );
        return response.getBody();
    }

    public DomicilioDTO updateDomicilio(Long id, DomicilioDTO dto) {
        ResponseEntity<DomicilioDTO> response = restTemplate.exchange(
                serverApiUrl + "/domicilios/" + id,
                HttpMethod.PUT,
                createAuthEntity(dto),
                DomicilioDTO.class
        );
        return response.getBody();
    }

    public void deleteDomicilio(Long id) {
        restTemplate.exchange(
                serverApiUrl + "/domicilios/" + id,
                HttpMethod.DELETE,
                createAuthEntity(null),
                Void.class
        );
    }
}
