package com.example.ecommerce.domain.exception;

public class RutinaSinMinimoDeArticulosException extends ReglaDominioException {

    public RutinaSinMinimoDeArticulosException() {
        super("La rutina de cuidado debe tener mínimo 2 artículos");
    }
}
