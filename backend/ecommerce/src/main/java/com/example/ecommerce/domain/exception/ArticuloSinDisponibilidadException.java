package com.example.ecommerce.domain.exception;

public class ArticuloSinDisponibilidadException extends ReglaDominioException {

    public ArticuloSinDisponibilidadException() {
        super("No se puede agregar a la rutina un artículo sin disponibilidad");
    }
}
