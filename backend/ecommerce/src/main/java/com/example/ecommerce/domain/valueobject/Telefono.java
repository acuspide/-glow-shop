package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.TelefonoInvalidoException;

import java.util.regex.Pattern;

/**
 * Número de teléfono: de 7 a 15 dígitos, con un "+" opcional al inicio.
 */
public record Telefono(String valor) {

    private static final Pattern PATRON = Pattern.compile("^\\+?\\d{7,15}$");

    public Telefono {
        if (valor == null || !PATRON.matcher(valor.trim()).matches()) {
            throw new TelefonoInvalidoException();
        }
        valor = valor.trim();
    }
}
