package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.FechaVencimientoRequeridaException;

import java.time.LocalDate;

public class FechaVencimiento {
    private final LocalDate fecha;

    public FechaVencimiento(LocalDate fecha) {
        validarFecha(fecha);
        this.fecha = fecha;
    }

    private void validarFecha(LocalDate fecha) {

        if (fecha == null) {
            throw new FechaVencimientoRequeridaException();
        }
    }

    public boolean estaVencida() {
        return LocalDate.now().isAfter(fecha);
    }
}
