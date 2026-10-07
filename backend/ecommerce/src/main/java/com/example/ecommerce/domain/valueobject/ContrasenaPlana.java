package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.ContrasenaDebilException;
import com.example.ecommerce.domain.exception.ContrasenaRequeridaException;

/**
 * Contraseña tal como la escribe el usuario, antes de encriptarla.
 * Solo existe para validarla; nunca se guarda ni se imprime.
 *
 * Regla: mínimo 8 caracteres, al menos una mayúscula y al menos un número.
 */
public record ContrasenaPlana(String valor) {

    private static final int LONGITUD_MINIMA = 8;

    public ContrasenaPlana {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ContrasenaRequeridaException();
        }
        if (!esSegura(valor)) {
            throw new ContrasenaDebilException();
        }
    }

    private static boolean esSegura(String valor) {
        return valor.length() >= LONGITUD_MINIMA
                && valor.chars().anyMatch(Character::isUpperCase)
                && valor.chars().anyMatch(Character::isDigit);
    }

    @Override
    public String toString() {
        return "ContrasenaPlana[****]"; // evita que salga en logs
    }
}
