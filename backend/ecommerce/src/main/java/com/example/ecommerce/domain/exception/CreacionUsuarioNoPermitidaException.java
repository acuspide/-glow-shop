package com.example.ecommerce.domain.exception;

public class CreacionUsuarioNoPermitidaException extends ReglaDominioException {
    public CreacionUsuarioNoPermitidaException() {
        super("Solo un administrador activo puede crear usuarios con rol de vendedor o administrador.");
    }
}
