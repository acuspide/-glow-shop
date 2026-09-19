package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.PrecioInvalidoException;

import java.math.BigDecimal;

public class Precio {
    private final BigDecimal valor;

    public Precio(BigDecimal valor) {
        validarPrecio(valor);
        this.valor = valor;
    }

    private void validarPrecio(BigDecimal valor) {

        if (valor == null || valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new PrecioInvalidoException();
        }
    }
}
