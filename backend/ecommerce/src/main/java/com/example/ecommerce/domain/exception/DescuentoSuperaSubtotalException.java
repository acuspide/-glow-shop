package com.example.ecommerce.domain.exception;

public class DescuentoSuperaSubtotalException extends ReglaDominioException{
    public DescuentoSuperaSubtotalException() {super("El descuento no puede ser mayor que la suma de los detalles del pedido.");
    }
}
