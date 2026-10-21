package com.example.ecommerce.domain.exception;

public class PuntuacionInvalidaException extends ReglaDominioException {
    public PuntuacionInvalidaException() {
        super("La calificación debe estar entre 1 y 5 estrellas.");
    }
}
