package com.example.ecommerce.domain.exception;

public class VendedorYaTieneTiendaException extends ReglaDominioException {
    public VendedorYaTieneTiendaException() {
        super("El vendedor ya tiene una tienda registrada.");
    }
}
