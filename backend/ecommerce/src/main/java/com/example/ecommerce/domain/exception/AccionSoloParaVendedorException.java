package com.example.ecommerce.domain.exception;

public class AccionSoloParaVendedorException extends ReglaDominioException {
    public AccionSoloParaVendedorException() {
        super("Solo un vendedor activo puede realizar esta acción.");
    }
}
