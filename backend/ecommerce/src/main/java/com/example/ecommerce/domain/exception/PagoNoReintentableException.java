package com.example.ecommerce.domain.exception;

public class PagoNoReintentableException extends ReglaDominioException {
    public PagoNoReintentableException() {
        super("Solo se puede reintentar un pago rechazado.");
    }
}
