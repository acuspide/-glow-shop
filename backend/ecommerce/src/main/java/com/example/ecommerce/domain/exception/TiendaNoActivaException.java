package com.example.ecommerce.domain.exception;

import com.example.ecommerce.domain.valueobject.EstadoTienda;

public class TiendaNoActivaException extends ReglaDominioException {
    public TiendaNoActivaException(EstadoTienda estadoActual) {
        super("La tienda debe estar activa para publicar artículos. Estado actual: " + estadoActual + ".");
    }
}
