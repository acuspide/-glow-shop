package com.example.ecommerce.domain.exception;

public class NombreCategoriaRequeridoException extends ReglaDominioException {
    public NombreCategoriaRequeridoException() {
        super("El nombre de la categoría es obligatorio.");
    }

}
