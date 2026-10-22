package com.example.ecommerce.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CarritoResponse(
        String id,
        String clienteId,
        List<ItemCarritoResponse> items,
        BigDecimal total
) {
}