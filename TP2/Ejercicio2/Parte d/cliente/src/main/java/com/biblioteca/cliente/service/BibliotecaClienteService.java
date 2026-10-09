package com.biblioteca.cliente.service;

import com.biblioteca.cliente.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class BibliotecaClienteService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${servidor.api.url:http://localhost:8080/api}")
    private String serverApiUrl;

    // ==================== NOTIFICACIONES & SCHEDULING ====================
    public List<EmailLogDTO> getHistorialNotificaciones() {
        ResponseEntity<List<EmailLogDTO>> response = restTemplate.exchange(
                serverApiUrl + "/notificaciones/historial",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public Map<String, Object> ejecutarVencimientos() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                serverApiUrl + "/notificaciones/ejecutar-vencimientos",
                HttpMethod.POST,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public Map<String, Object> ejecutarCumpleanios() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                serverApiUrl + "/notificaciones/ejecutar-cumpleanios",
                HttpMethod.POST,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public Map<String, Object> getResumenHoy() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                serverApiUrl + "/notificaciones/resumen-hoy",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

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

    // ==================== REPORTES (INCISO 2.d: PDF & EXCEL) ====================
    public byte[] descargarAlquileresPdf() {
        return restTemplate.getForObject(serverApiUrl + "/reportes/alquileres/pdf", byte[].class);
    }

    public byte[] descargarLibrosDisponiblesExcel() {
        return restTemplate.getForObject(serverApiUrl + "/reportes/libros-disponibles/excel", byte[].class);
    }

    public Map<String, Object> getEstadisticasReportes() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                serverApiUrl + "/reportes/estadisticas",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public List<PersonaDTO> getPersonasConAlquileres() {
        ResponseEntity<List<PersonaDTO>> response = restTemplate.exchange(
                serverApiUrl + "/reportes/alquileres/datos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    public List<LibroDTO> getLibrosDisponibles() {
        ResponseEntity<List<LibroDTO>> response = restTemplate.exchange(
                serverApiUrl + "/reportes/libros-disponibles/datos",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }
}
