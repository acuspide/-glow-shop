package com.example.ecommerce.domain.exception;

public class CodigoCuponRequeridoException extends ReglaDominioException {
    public CodigoCuponRequeridoException() {
        super("El cupón debe tener un código.");
    }
}
