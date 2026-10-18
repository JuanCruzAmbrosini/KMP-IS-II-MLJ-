package ingsoftware.gatinder.controller.rest;

import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ingsoftware.gatinder.dto.ErrorResponseDto;
import ingsoftware.gatinder.service.ErrorService;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice(basePackages = "ingsoftware.gatinder.controller.rest")
public class GlobalRestExceptionHandler {

    @ExceptionHandler(ErrorService.class)
    public ResponseEntity<ErrorResponseDto> handleErrorService(ErrorService ex, HttpServletRequest request) {
        String msg = ex.getMessage();
        HttpStatus status = (msg != null && msg.toLowerCase().contains("no se encontró"))
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;

        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(msg)
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDto, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex, HttpServletRequest request) {
        ErrorResponseDto errorDto = ErrorResponseDto.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage() != null ? ex.getMessage() : "Error interno del servidor")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
