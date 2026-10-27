package com.example.catalog.service.impl;

import com.example.catalog.exception.ResourceNotFoundException;
import com.example.catalog.model.Book;
import com.example.catalog.repository.BookRepository;
import com.example.catalog.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Libro no encontrado con ID: " + id));
    }

    @Override
    public Book createBook(Book book) {
        if (book.getIsbn() != null && bookRepository.existsByIsbn(book.getIsbn())) {
            throw new IllegalArgumentException("Ya existe un libro registrado con el ISBN: " + book.getIsbn());
        }
        // Asegurar que el ID sea null al crear para autogeneración
        book.setId(null);
        return bookRepository.save(book);
    }

    @Override
    public Book updateBook(Long id, Book updatedBook) {
        Book existingBook = getBookById(id);

        // Si se cambia el ISBN, verificar que no colisione con otro libro
        if (updatedBook.getIsbn() != null && !updatedBook.getIsbn().equals(existingBook.getIsbn())) {
            if (bookRepository.existsByIsbn(updatedBook.getIsbn())) {
                throw new IllegalArgumentException("Ya existe otro libro con el ISBN: " + updatedBook.getIsbn());
            }
        }

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setPrice(updatedBook.getPrice());
        existingBook.setStock(updatedBook.getStock());

        return bookRepository.save(existingBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book existingBook = getBookById(id);
        bookRepository.delete(existingBook);
    }
}

