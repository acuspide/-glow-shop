package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.ContrasenaRequeridaException;

/**
 * Contraseña tal como la escribe el usuario, antes de encriptarla.
 * Solo existe para validarla; nunca se guarda ni se imprime.
 */
public record ContrasenaPlana(String valor) {

    public ContrasenaPlana {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ContrasenaRequeridaException();
        }
    }

    @Override
    public String toString() {
        return "ContrasenaPlana[****]"; // evita que salga en logs
    }
}
