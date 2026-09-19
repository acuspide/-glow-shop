package com.example.ecommerce.domain.exception;

public class StockNegativoException extends RuntimeException {
    public StockNegativoException() {
        super("La cantidad disponible no puede ser negativa.");
    }
}
