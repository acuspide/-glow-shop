package com.example.ecommerce.application.dto.response;

import java.math.BigDecimal;

public record ItemCarritoResponse(
        String articuloId,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}