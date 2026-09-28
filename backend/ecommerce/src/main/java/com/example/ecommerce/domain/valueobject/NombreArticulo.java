package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.NombreArticuloRequeridoException;

public record NombreArticulo(String valor) {

    public NombreArticulo {
        if (valor == null || valor.trim().isEmpty()) {
            throw new NombreArticuloRequeridoException();
        }
    }
}