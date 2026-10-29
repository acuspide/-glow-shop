package com.example.ecommerce.domain.exception;

public class DescripcionTiendaMuyLargaException extends ReglaDominioException {
    public DescripcionTiendaMuyLargaException(int maximo) {
        super("La descripción de la tienda no puede superar " + maximo + " caracteres.");
    }
}
