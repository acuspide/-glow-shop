package com.example.ecommerce.domain.exception;

public class ArticuloNoRecibidoException extends ReglaDominioException {
    public ArticuloNoRecibidoException() {
        super("Solo puede calificar un artículo quien lo compró y ya lo recibió.");
    }
}
