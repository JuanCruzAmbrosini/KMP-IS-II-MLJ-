package com.biblioteca.cliente.controller;

import com.biblioteca.cliente.dto.*;
import com.biblioteca.cliente.service.BibliotecaClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cliente/api")
@CrossOrigin(origins = "*")
public class ClienteApiController {

    @Autowired
    private BibliotecaClienteService clienteService;

    // ==================== PERSONAS ====================
    @GetMapping("/personas")
    public List<PersonaDTO> getPersonas() {
        return clienteService.getPersonas();
    }

    @GetMapping("/personas/{id}")
    public PersonaDTO getPersonaById(@PathVariable Long id) {
        return clienteService.getPersonaById(id);
    }

    @PostMapping("/personas")
    @ResponseStatus(HttpStatus.CREATED)
    public PersonaDTO createPersona(@RequestBody PersonaDTO dto) {
        return clienteService.createPersona(dto);
    }

    @PutMapping("/personas/{id}")
    public PersonaDTO updatePersona(@PathVariable Long id, @RequestBody PersonaDTO dto) {
        return clienteService.updatePersona(id, dto);
    }

    @DeleteMapping("/personas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePersona(@PathVariable Long id) {
        clienteService.deletePersona(id);
    }

    // ==================== LIBROS ====================
    @GetMapping("/libros")
    public List<LibroDTO> getLibros() {
        return clienteService.getLibros();
    }

    @GetMapping("/libros/{id}")
    public LibroDTO getLibroById(@PathVariable Long id) {
        return clienteService.getLibroById(id);
    }

    @PostMapping("/libros")
    @ResponseStatus(HttpStatus.CREATED)
    public LibroDTO createLibro(@RequestBody LibroDTO dto) {
        return clienteService.createLibro(dto);
    }

    @PutMapping("/libros/{id}")
    public LibroDTO updateLibro(@PathVariable Long id, @RequestBody LibroDTO dto) {
        return clienteService.updateLibro(id, dto);
    }

    @DeleteMapping("/libros/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLibro(@PathVariable Long id) {
        clienteService.deleteLibro(id);
    }

    // ==================== AUTORES ====================
    @GetMapping("/autores")
    public List<AutorDTO> getAutores() {
        return clienteService.getAutores();
    }

    @GetMapping("/autores/{id}")
    public AutorDTO getAutorById(@PathVariable Long id) {
        return clienteService.getAutorById(id);
    }

    @PostMapping("/autores")
    @ResponseStatus(HttpStatus.CREATED)
    public AutorDTO createAutor(@RequestBody AutorDTO dto) {
        return clienteService.createAutor(dto);
    }

    @PutMapping("/autores/{id}")
    public AutorDTO updateAutor(@PathVariable Long id, @RequestBody AutorDTO dto) {
        return clienteService.updateAutor(id, dto);
    }

    @DeleteMapping("/autores/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAutor(@PathVariable Long id) {
        clienteService.deleteAutor(id);
    }

    // ==================== LOCALIDADES ====================
    @GetMapping("/localidades")
    public List<LocalidadDTO> getLocalidades() {
        return clienteService.getLocalidades();
    }

    @GetMapping("/localidades/{id}")
    public LocalidadDTO getLocalidadById(@PathVariable Long id) {
        return clienteService.getLocalidadById(id);
    }

    @PostMapping("/localidades")
    @ResponseStatus(HttpStatus.CREATED)
    public LocalidadDTO createLocalidad(@RequestBody LocalidadDTO dto) {
        return clienteService.createLocalidad(dto);
    }

    @PutMapping("/localidades/{id}")
    public LocalidadDTO updateLocalidad(@PathVariable Long id, @RequestBody LocalidadDTO dto) {
        return clienteService.updateLocalidad(id, dto);
    }

    @DeleteMapping("/localidades/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocalidad(@PathVariable Long id) {
        clienteService.deleteLocalidad(id);
    }

    // ==================== DOMICILIOS ====================
    @GetMapping("/domicilios")
    public List<DomicilioDTO> getDomicilios() {
        return clienteService.getDomicilios();
    }

    @GetMapping("/domicilios/{id}")
    public DomicilioDTO getDomicilioById(@PathVariable Long id) {
        return clienteService.getDomicilioById(id);
    }

    @PostMapping("/domicilios")
    @ResponseStatus(HttpStatus.CREATED)
    public DomicilioDTO createDomicilio(@RequestBody DomicilioDTO dto) {
        return clienteService.createDomicilio(dto);
    }

    @PutMapping("/domicilios/{id}")
    public DomicilioDTO updateDomicilio(@PathVariable Long id, @RequestBody DomicilioDTO dto) {
        return clienteService.updateDomicilio(id, dto);
    }

    @DeleteMapping("/domicilios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDomicilio(@PathVariable Long id) {
        clienteService.deleteDomicilio(id);
    }
}

