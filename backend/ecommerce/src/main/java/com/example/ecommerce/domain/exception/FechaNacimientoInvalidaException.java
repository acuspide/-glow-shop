package com.example.ecommerce.domain.exception;

public class FechaNacimientoInvalidaException extends ReglaDominioException {
    public FechaNacimientoInvalidaException() {
        super("La fecha de nacimiento es obligatoria, no puede ser futura ni anterior a 1900.");
    }
}
