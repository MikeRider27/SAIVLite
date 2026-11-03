package com.saiv.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public record ErrorBody(LocalDateTime timestamp, int status, String message, Map<String, String> fields) {}

    private ResponseEntity<ErrorBody> body(HttpStatus status, String msg, Map<String, String> fields) {
        return ResponseEntity.status(status).body(new ErrorBody(LocalDateTime.now(), status.value(), msg, fields));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorBody> api(ApiException ex) {
        return body(ex.getStatus(), ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBody> validation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(f -> f.getField(), f -> String.valueOf(f.getDefaultMessage()), (a, b) -> a));
        return body(HttpStatus.BAD_REQUEST, "Datos inválidos", fields);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorBody> integrity(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad: {}", ex.getMostSpecificCause().getMessage());
        return body(HttpStatus.CONFLICT, "La operación viola una restricción de datos (registro duplicado o en uso)", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorBody> denied(AccessDeniedException ex) {
        return body(HttpStatus.FORBIDDEN, "No tiene permiso para esta acción", null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorBody> auth(AuthenticationException ex) {
        return body(HttpStatus.UNAUTHORIZED, "No autenticado", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorBody> generic(Exception ex) {
        log.error("Error no controlado", ex);
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", null);
    }
}
