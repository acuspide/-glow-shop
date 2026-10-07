package com.example.ecommerce.domain.exception;

public class ArticuloNoPublicadoParaVentaException extends ReglaDominioException {
    public ArticuloNoPublicadoParaVentaException() {
        super("No se puede vender un artículo agotado.");
    }
}
