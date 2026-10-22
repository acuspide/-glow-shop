package com.example.ecommerce.domain.exception;

public class ArticuloEliminadoException extends ReglaDominioException {
    public ArticuloEliminadoException() {
        super("El artículo está eliminado y no puede modificarse.");
    }
}
