package com.example.ecommerce.domain.exception;

import com.example.ecommerce.domain.valueobject.EstadoTienda;

public class TiendaNoPendienteException extends ReglaDominioException {
    public TiendaNoPendienteException(EstadoTienda estadoActual) {
        super("Solo se puede aprobar una tienda pendiente. Estado actual: " + estadoActual + ".");
    }
}
