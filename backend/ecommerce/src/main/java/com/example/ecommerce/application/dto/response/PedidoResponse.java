package com.example.ecommerce.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record PedidoResponse(
        String id,
        String clienteId,
        String tiendaId,
        String estado,
        List<DetallePedidoResponse> detalles,
        BigDecimal subtotal,
        BigDecimal total
) {
}
