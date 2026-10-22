package com.example.ecommerce.domain.exception;

public class AccionSoloParaClienteException extends ReglaDominioException {
    public AccionSoloParaClienteException() {
        super("Solo un cliente activo puede realizar esta acción.");
    }
}
