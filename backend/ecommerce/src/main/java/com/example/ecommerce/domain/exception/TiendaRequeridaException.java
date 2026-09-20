package com.example.ecommerce.domain.exception;

public class TiendaRequeridaException extends RuntimeException {
    public TiendaRequeridaException() {
        super("La tienda es obligatoria.");
    }
}
