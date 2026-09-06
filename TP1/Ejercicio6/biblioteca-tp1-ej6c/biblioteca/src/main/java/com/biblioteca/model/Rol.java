package com.biblioteca.model;

/**
 * Roles de acceso al sistema.
 * ADMIN: administra Autores, Editoriales, Libros y Prestamos.
 * USUARIO: puede ver el catalogo de libros y sus propios prestamos.
 */
public enum Rol {
    ADMIN,
    USUARIO
}
