package com.example.ecommerce.domain.exception;

public class PrecioDelArticuloCambioException extends ReglaDominioException {
    public PrecioDelArticuloCambioException(String articuloId) {
        super("El precio del artículo con id: " + articuloId + " cambió desde que se agregó al carrito.");
    }
}