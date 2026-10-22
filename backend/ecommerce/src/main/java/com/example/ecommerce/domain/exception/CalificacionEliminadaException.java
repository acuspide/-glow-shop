package com.example.ecommerce.domain.exception;

public class CalificacionEliminadaException extends ReglaDominioException {
    public CalificacionEliminadaException() {
        super("La calificación ya fue eliminada.");
    }
}
