package com.example.ecommerce.domain.exception;

public class CodigoRecuperacionInvalidoException extends ReglaDominioException {
    public CodigoRecuperacionInvalidoException() {
        super("El código de recuperación es incorrecto, ya se usó o expiró.");
    }
}
