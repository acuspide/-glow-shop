package com.example.ecommerce.domain.exception;

public class ArticuloNoEncontradoException extends RecursoNoEncontradoException {

    public ArticuloNoEncontradoException(String id) {
        super("el artículo", id);
    }
}
