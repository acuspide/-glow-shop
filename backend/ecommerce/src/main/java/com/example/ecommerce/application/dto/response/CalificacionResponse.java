package com.example.ecommerce.application.dto.response;

import java.time.LocalDateTime;

public record CalificacionResponse(
        String id,
        String articuloId,
        String clienteId,
        int puntuacion,
        String comentario,
        LocalDateTime fecha
) {
}
