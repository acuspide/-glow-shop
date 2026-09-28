package com.example.ecommerce.infrastructure.rest.exception;

import com.example.ecommerce.domain.exception.ReglaDominioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ReglaDominioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaDominio(
            ReglaDominioException ex) {

        return construirRespuesta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(
            NoSuchElementException ex) {

        return construirRespuesta(
                HttpStatus.NOT_FOUND,
                "El recurso solicitado no existe"
        );
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(
            HttpStatus status,
            String mensaje) {

        Map<String, Object> cuerpo = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "mensaje", mensaje
        );

        return ResponseEntity
                .status(status)
                .body(cuerpo);
    }

}
