package com.example.ecommerce.domain.exception;

public class PorcentajeDescuentoInvalidoException extends ReglaDominioException {
    public PorcentajeDescuentoInvalidoException() {
        super("El porcentaje de descuento debe estar entre 1 y 100.");
    }
}
