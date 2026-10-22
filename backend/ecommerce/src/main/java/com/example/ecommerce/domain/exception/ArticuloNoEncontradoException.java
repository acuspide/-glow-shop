package com.example.ecommerce.domain.exception;

public class ArticuloNoEncontradoException extends ReglaDominioException {

    public ArticuloNoEncontradoException(String id) {
        super("No se encontró el artículo con id: " + id);
    }
}
