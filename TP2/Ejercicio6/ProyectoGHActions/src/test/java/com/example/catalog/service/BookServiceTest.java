package com.example.catalog.service;

import com.example.catalog.exception.ResourceNotFoundException;
import com.example.catalog.model.Book;
import com.example.catalog.repository.BookRepository;
import com.example.catalog.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book sampleBook;

    @BeforeEach
    void setUp() {
        sampleBook = new Book(1L, "Clean Code", "Robert C. Martin", "978-0132350884", 45.99, 10);
    }

    @Test
    @DisplayName("Debe retornar la lista completa de libros")
    void testGetAllBooks() {
        Book book2 = new Book(2L, "The Pragmatic Programmer", "Andrew Hunt & David Thomas", "978-0201616224", 50.00, 5);
        when(bookRepository.findAll()).thenReturn(Arrays.asList(sampleBook, book2));

        List<Book> books = bookService.getAllBooks();

        assertThat(books).hasSize(2);
        assertThat(books.get(0).getTitle()).isEqualTo("Clean Code");
        verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("Debe retornar un libro existente por su ID")
    void testGetBookByIdSuccess() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        Book found = bookService.getBookById(1L);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(1L);
        assertThat(found.getTitle()).isEqualTo("Clean Code");
        verify(bookRepository).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el libro no existe")
    void testGetBookByIdNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBookById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Libro no encontrado con ID: 99");

        verify(bookRepository).findById(99L);
    }

    @Test
    @DisplayName("Debe guardar y retornar un nuevo libro exitosamente")
    void testCreateBookSuccess() {
        Book newBook = new Book("Refactoring", "Martin Fowler", "978-0201485677", 48.00, 7);
        Book savedBook = new Book(3L, "Refactoring", "Martin Fowler", "978-0201485677", 48.00, 7);

        when(bookRepository.existsByIsbn("978-0201485677")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(savedBook);

        Book result = bookService.createBook(newBook);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getTitle()).isEqualTo("Refactoring");
        verify(bookRepository).existsByIsbn("978-0201485677");
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException al intentar crear un libro con ISBN duplicado")
    void testCreateBookDuplicateIsbn() {
        Book duplicateBook = new Book("Clean Architecture", "Robert C. Martin", "978-0132350884", 42.00, 8);
        when(bookRepository.existsByIsbn("978-0132350884")).thenReturn(true);

        assertThatThrownBy(() -> bookService.createBook(duplicateBook))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un libro registrado con el ISBN");

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Debe actualizar un libro existente correctamente")
    void testUpdateBookSuccess() {
        Book updateInfo = new Book(null, "Clean Code 2nd Ed", "Robert C. Martin", "978-0132350884", 49.99, 12);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Book updated = bookService.updateBook(1L, updateInfo);

        assertThat(updated.getTitle()).isEqualTo("Clean Code 2nd Ed");
        assertThat(updated.getPrice()).isEqualTo(49.99);
        assertThat(updated.getStock()).isEqualTo(12);
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Debe eliminar un libro existente")
    void testDeleteBookSuccess() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        bookService.deleteBook(1L);

        verify(bookRepository).findById(1L);
        verify(bookRepository).delete(sampleBook);
    }
}

