package com.example.ecommerce.application.dto.response;

import java.math.BigDecimal;

public record PagoResponse(
        String id,
        String pedidoId,
        BigDecimal monto,
        String medioPago,
        String estado,
        String transaccionId,
        String motivoRechazo
) {
}
