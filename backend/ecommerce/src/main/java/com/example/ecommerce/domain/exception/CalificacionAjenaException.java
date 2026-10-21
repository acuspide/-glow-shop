package com.example.ecommerce.domain.exception;

public class CalificacionAjenaException extends ReglaDominioException {
    public CalificacionAjenaException() {
        super("Solo el autor de la calificación puede modificarla.");
    }
}
