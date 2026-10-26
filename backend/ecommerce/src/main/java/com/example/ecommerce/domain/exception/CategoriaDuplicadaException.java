package com.example.ecommerce.domain.exception;

public class CategoriaDuplicadaException extends ReglaDominioException {
    public CategoriaDuplicadaException() {
        super("Ya existe una categoría con ese nombre.");
    }
}
