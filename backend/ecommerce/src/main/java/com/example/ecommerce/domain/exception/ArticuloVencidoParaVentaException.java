package com.example.ecommerce.domain.exception;

public class ArticuloVencidoParaVentaException extends ReglaDominioException {
    public ArticuloVencidoParaVentaException() {
        super("No se puede vender un artículo que esté vencido.");
    }
}
