package com.example.ecommerce.domain.exception;

public class CategoriaRequeridaException extends RuntimeException {

    public CategoriaRequeridaException() {
        super("El artículo debe pertenecer a una categoría.");
    }
}
