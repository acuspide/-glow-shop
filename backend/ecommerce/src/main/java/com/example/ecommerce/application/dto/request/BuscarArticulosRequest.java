package com.example.ecommerce.application.dto.request;

import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;

import java.math.BigDecimal;

public record BuscarArticulosRequest(
        String texto,
        Long categoriaId,
        Long marcaId,
        Long tiendaId,
        BigDecimal precioMinimo,
        BigDecimal precioMaximo,
        Tono tono,
        TipoPiel tipoPiel
) {
}
