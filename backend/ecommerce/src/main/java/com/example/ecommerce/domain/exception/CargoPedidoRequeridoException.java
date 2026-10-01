package com.example.ecommerce.domain.exception;

public class CargoPedidoRequeridoException extends ReglaDominioException {
    public CargoPedidoRequeridoException() {
        super("Los impuestos y el costo de envío del pedido son obligatorios.");
    }
}
