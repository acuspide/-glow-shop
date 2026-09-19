package com.example.ecommerce.domain.exception;

public class FechaVencimientoRequeridaException extends RuntimeException {
    public FechaVencimientoRequeridaException() {
        super("La fecha de vencimiento es obligatoria.");
    }
}
