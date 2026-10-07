package com.example.ecommerce.domain.exception;

public class ArticuloAgotadoException extends ReglaDominioException {
    public ArticuloAgotadoException() {
        super("No se puede vender un artículo agotado.");
    }
}
