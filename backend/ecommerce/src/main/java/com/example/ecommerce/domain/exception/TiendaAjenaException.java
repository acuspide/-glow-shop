package com.example.ecommerce.domain.exception;

public class TiendaAjenaException extends ReglaDominioException {
    public TiendaAjenaException() {
        super("Solo el vendedor dueño de la tienda puede realizar esta acción.");
    }
}
