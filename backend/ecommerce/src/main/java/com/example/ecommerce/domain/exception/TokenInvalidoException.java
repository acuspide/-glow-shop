package com.example.ecommerce.domain.exception;

public class TokenInvalidoException extends ReglaDominioException {
    public TokenInvalidoException() {
        super("El token de sesión no es válido o ya expiró.");
    }
}
