package com.example.ecommerce.domain.exception;

public class MontoPagoRequeridoException extends ReglaDominioException {
    public MontoPagoRequeridoException() {
        super("El pago debe tener un monto.");
    }
}
