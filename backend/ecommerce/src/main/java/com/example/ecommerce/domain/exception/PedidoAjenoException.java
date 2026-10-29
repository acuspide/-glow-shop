package com.example.ecommerce.domain.exception;

public class PedidoAjenoException extends ReglaDominioException {
    public PedidoAjenoException() {
        super("El pedido no pertenece al cliente que intenta usarlo.");
    }
}