package com.example.ecommerce.domain.exception;

public class ComentarioInvalidoException extends ReglaDominioException {
    public ComentarioInvalidoException() {
        super("El comentario es obligatorio y no puede superar los 500 caracteres.");
    }
}
