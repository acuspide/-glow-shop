package com.uniquindio.ecommerce.domain.valueobject;

import com.uniquindio.ecommerce.domain.exception.ReglaDominioException;

public record Precio(double monto, String moneda) {
    public Precio{
        if (monto < 0){
            throw new ReglaDominioException("el precio no puede ser negativo");
        }

    }
}
