package com.example.ecommerce.domain.exception;

public class NombreTiendaRequeridoException extends ReglaDominioException {
    public NombreTiendaRequeridoException() {
        super("El nombre de la tienda es obligatorio.");
    }
}
