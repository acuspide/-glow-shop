package com.example.ecommerce.domain.exception;

public class MedioPagoRequeridoException extends ReglaDominioException {
    public MedioPagoRequeridoException() {
        super("El pago debe tener un medio de pago.");
    }
}