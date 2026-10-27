package com.example.catalog.controller;

import com.example.catalog.exception.ResourceNotFoundException;
import com.example.catalog.model.Book;
import com.example.catalog.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/books - Debe retornar lista de libros con status 200")
    void testGetAllBooks() throws Exception {
        Book b1 = new Book(1L, "Clean Code", "Robert C. Martin", "978-0132350884", 45.99, 10);
        Book b2 = new Book(2L, "Effective Java", "Joshua Bloch", "978-0134685991", 55.00, 15);

        when(bookService.getAllBooks()).thenReturn(Arrays.asList(b1, b2));

        mockMvc.perform(get("/api/books")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Clean Code")))
                .andExpect(jsonPath("$[1].title", is("Effective Java")));
    }

    @Test
    @DisplayName("GET /api/books/{id} - Debe retornar libro cuando existe con status 200")
    void testGetBookByIdFound() throws Exception {
        Book book = new Book(1L, "Clean Code", "Robert C. Martin", "978-0132350884", 45.99, 10);
        when(bookService.getBookById(1L)).thenReturn(book);

        mockMvc.perform(get("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Clean Code")))
                .andExpect(jsonPath("$.author", is("Robert C. Martin")));
    }

    @Test
    @DisplayName("GET /api/books/{id} - Debe retornar 404 cuando el libro no existe")
    void testGetBookByIdNotFound() throws Exception {
        when(bookService.getBookById(99L)).thenThrow(new ResourceNotFoundException("Libro no encontrado con ID: 99"));

        mockMvc.perform(get("/api/books/99")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Libro no encontrado con ID: 99")));
    }

    @Test
    @DisplayName("POST /api/books - Debe crear libro con status 201 y header Location")
    void testCreateBookValid() throws Exception {
        Book inputBook = new Book(null, "Domain-Driven Design", "Eric Evans", "978-0321125217", 60.0, 5);
        Book savedBook = new Book(10L, "Domain-Driven Design", "Eric Evans", "978-0321125217", 60.0, 5);

        when(bookService.createBook(any(Book.class))).thenReturn(savedBook);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputBook)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.title", is("Domain-Driven Design")));
    }

    @Test
    @DisplayName("POST /api/books - Debe fallar con 400 Bad Request si faltan datos requeridos")
    void testCreateBookInvalid() throws Exception {
        // Objeto sin título ni autor ni precio
        Book invalidBook = new Book();

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBook)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    @DisplayName("PUT /api/books/{id} - Debe actualizar y retornar status 200")
    void testUpdateBook() throws Exception {
        Book updateData = new Book(null, "Clean Code: Revised", "Robert C. Martin", "978-0132350884", 48.00, 15);
        Book updatedBook = new Book(1L, "Clean Code: Revised", "Robert C. Martin", "978-0132350884", 48.00, 15);

        when(bookService.updateBook(eq(1L), any(Book.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Clean Code: Revised")));
    }

    @Test
    @DisplayName("DELETE /api/books/{id} - Debe eliminar y retornar 204 No Content")
    void testDeleteBook() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/books/{id} - Debe retornar 404 si el libro no existe")
    void testDeleteBookNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Libro no encontrado con ID: 99")).when(bookService).deleteBook(99L);

        mockMvc.perform(delete("/api/books/99"))
                .andExpect(status().isNotFound());
    }
}

