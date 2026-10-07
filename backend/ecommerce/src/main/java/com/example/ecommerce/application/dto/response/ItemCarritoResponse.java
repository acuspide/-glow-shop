package com.example.ecommerce.application.dto.response;

import java.math.BigDecimal;

public record ItemCarritoResponse(
        long articuloId,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}