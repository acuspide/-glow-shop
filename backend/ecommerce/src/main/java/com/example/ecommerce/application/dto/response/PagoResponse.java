package com.example.ecommerce.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        String id,
        String pedidoId,
        BigDecimal monto,
        String medioPago,
        LocalDateTime fechaPago,
        String estado,
        String transaccionId,
        String motivoRechazo
) {
}
