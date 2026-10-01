package com.example.ecommerce.domain.exception;

public class PagoNoRegistrableException extends ReglaDominioException {
    public PagoNoRegistrableException() {super("El pago solo puede registrarse una vez y en un pedido confirmado.");
    }
}