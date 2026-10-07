package com.example.ecommerce.domain.exception;

public class PedidoNoModificableException extends ReglaDominioException{
    public PedidoNoModificableException() {super("El pedido solo puede modificarse mientras está en estado PENDIENTE.");
    }
}
