package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.ParametroInvalidoException;

/**
 * Cuántos artículos devuelve el ranking de más vendidos: de 1 a 20.
 */
public record LimiteRanking(int valor) {

    public static final int LIMITE_MAXIMO = 20;

    public LimiteRanking {
        if (valor < 1 || valor > LIMITE_MAXIMO) {
            throw new ParametroInvalidoException("El límite debe estar entre 1 y " + LIMITE_MAXIMO);
        }
    }
}
