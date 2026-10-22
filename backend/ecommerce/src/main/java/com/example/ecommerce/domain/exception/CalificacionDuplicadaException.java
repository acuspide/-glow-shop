package com.example.ecommerce.domain.exception;

public class CalificacionDuplicadaException extends ReglaDominioException {
    public CalificacionDuplicadaException() {
        super("Ya calificaste este artículo; puedes editar tu calificación.");
    }
}
