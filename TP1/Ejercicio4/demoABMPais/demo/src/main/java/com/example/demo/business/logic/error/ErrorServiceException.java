package com.example.demo.business.logic.error;

/*
 * ErrorServiceException: Esta clase representa una excepción personalizada que se utiliza para manejar errores específicos
 * en la capa de servicio de la aplicación. Al extender la clase Exception, permite lanzar y capturar esta excepción
 * en los métodos de servicio, proporcionando mensajes de error claros y específicos.
 */
public class ErrorServiceException extends Exception {
    
    public ErrorServiceException() {}

    public ErrorServiceException(String msg) {
        super(msg);
    }
}

