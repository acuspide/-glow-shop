package com.example.ecommerce.domain.exception;

public class PagoInvalidoException extends ReglaDominioException {
    public PagoInvalidoException() {super("El pago debe estar aprobado, pertenecer a este pedido y coincidir con su total.");
    }
}
