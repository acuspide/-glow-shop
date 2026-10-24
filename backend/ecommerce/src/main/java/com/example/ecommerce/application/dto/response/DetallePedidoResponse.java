package com.example.ecommerce.application.dto.response;

import java.math.BigDecimal;

public record DetallePedidoResponse(
        String articuloId,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal
) {
}
