package com.example.ecommerce.application.dto.request;

import com.example.ecommerce.domain.valueobject.EstadoPedido;
import jakarta.validation.constraints.NotNull;

public record ActualizarEstadoPedidoRequest(
        @NotNull(message = "El nuevo estado es obligatorio (EN_PREPARACION, ENVIADO o ENTREGADO)")
        EstadoPedido estado
) {
}
