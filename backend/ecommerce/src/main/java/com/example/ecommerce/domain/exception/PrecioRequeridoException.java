package com.example.ecommerce.domain.exception;

public class PrecioRequeridoException extends RuntimeException {
    public PrecioRequeridoException() {
        super("El artículo debe tener un precio.");
    }
}
