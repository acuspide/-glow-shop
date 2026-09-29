package com.example.ecommerce.domain.exception;

public class ArticuloNoEncontradoException extends ReglaDominioException {

    public ArticuloNoEncontradoException(long id) {
        super("No se encontró el artículo con id: " + id);
    }
}
