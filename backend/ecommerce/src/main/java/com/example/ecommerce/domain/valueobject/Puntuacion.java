package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.PuntuacionInvalidaException;

/**
 * Estrellas de una calificación: de 1 a 5 (RN76).
 */
public record Puntuacion(int valor) {

    public static final int MINIMA = 1;
    public static final int MAXIMA = 5;

    public Puntuacion {
        if (valor < MINIMA || valor > MAXIMA) {
            throw new PuntuacionInvalidaException();
        }
    }
}
