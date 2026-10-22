package com.example.ecommerce.domain.exception;

public class AutodesactivacionNoPermitidaException extends ReglaDominioException {
    public AutodesactivacionNoPermitidaException() {
        super("Un administrador no puede desactivar su propia cuenta.");
    }
}
