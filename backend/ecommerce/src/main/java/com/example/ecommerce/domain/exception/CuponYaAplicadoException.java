package com.example.ecommerce.domain.exception;

public class CuponYaAplicadoException extends ReglaDominioException {
    public CuponYaAplicadoException() {
        super("El pedido ya tiene un cupón aplicado.");
    }
}
