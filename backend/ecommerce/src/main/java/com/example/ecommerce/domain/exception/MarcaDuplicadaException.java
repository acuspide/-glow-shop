package com.example.ecommerce.domain.exception;

public class MarcaDuplicadaException extends ReglaDominioException {
    public MarcaDuplicadaException() {
        super("Ya existe una marca con ese nombre.");
    }
}
