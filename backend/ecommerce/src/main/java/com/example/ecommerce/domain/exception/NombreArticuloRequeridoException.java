package com.example.ecommerce.domain.exception;

public class NombreArticuloRequeridoException extends RuntimeException {
    public NombreArticuloRequeridoException() {
        super("El nombre del artículo es obligatorio.");
    }
}
