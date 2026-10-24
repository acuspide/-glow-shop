package com.example.ecommerce.domain.exception;

public class TransaccionRequeridaException extends ReglaDominioException {
    public TransaccionRequeridaException() {
        super("El pago debe tener el identificador de la transacción del sistema de pago.");
    }
}