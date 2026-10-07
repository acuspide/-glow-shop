package com.example.ecommerce.domain.exception;

public class TelefonoInvalidoException extends ReglaDominioException {
    public TelefonoInvalidoException() {
        super("El teléfono debe tener entre 7 y 15 dígitos y puede iniciar con +.");
    }
}
