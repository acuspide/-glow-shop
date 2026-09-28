package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.FechaVencimientoRequeridaException;

import java.time.LocalDate;

public record FechaVencimiento(LocalDate fecha) {

    public FechaVencimiento {
        if (fecha == null) {
            throw new FechaVencimientoRequeridaException();
        }
    }

    public boolean estaVencida() {
        return LocalDate.now().isAfter(fecha);
    }
}
