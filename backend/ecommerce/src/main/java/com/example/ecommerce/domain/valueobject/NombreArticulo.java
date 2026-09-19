package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.NombreArticuloRequeridoException;

public class NombreArticulo {

    private final String valor;

    public NombreArticulo(String valor) {
        validarNombre(valor);
        this.valor = valor;
    }

    private void validarNombre(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new NombreArticuloRequeridoException();
        }
    }
}
