package com.example.ecommerce.domain.exception;

public class UsuarioYaDesactivadoException extends ReglaDominioException {
    public UsuarioYaDesactivadoException() {
        super("El usuario ya está desactivado.");
    }
}
