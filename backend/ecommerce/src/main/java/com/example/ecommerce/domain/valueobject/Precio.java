package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.PrecioInvalidoException;

import java.math.BigDecimal;

public record Precio(BigDecimal valor) {

    public Precio {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new PrecioInvalidoException();
        }
    }
}