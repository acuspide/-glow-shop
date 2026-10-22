package com.example.ecommerce.domain.exception;

public class CarritoVacioException extends ReglaDominioException {
    public CarritoVacioException() {
        super("El carrito está vacío.");
    }
}