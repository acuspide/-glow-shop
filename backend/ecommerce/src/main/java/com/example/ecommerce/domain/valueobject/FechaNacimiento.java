package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.FechaNacimientoInvalidaException;

import java.time.LocalDate;

/**
 * Fecha de nacimiento: obligatoria, no futura y no anterior a 1900.
 */
public record FechaNacimiento(LocalDate valor) {

    private static final LocalDate MINIMA = LocalDate.of(1900, 1, 1);

    public FechaNacimiento {
        if (valor == null || valor.isAfter(LocalDate.now()) || valor.isBefore(MINIMA)) {
            throw new FechaNacimientoInvalidaException();
        }
    }
}
