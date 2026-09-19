package com.example.ecommerce.domain.exception;

public class ArticuloVencidoException extends RuntimeException {
    public ArticuloVencidoException() {
        super("No se puede publicar un artículo que esté vencido.");
    }
}
