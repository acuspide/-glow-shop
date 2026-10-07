package com.example.ecommerce.domain.exception;

public class PagoNoRegistradoException extends ReglaDominioException {
    public PagoNoRegistradoException() {super("El pedido no puede prepararse sin un pago confirmado registrado.");
    }
}
