package com.example.ecommerce.application.dto.response;

import java.util.List;

public record CalificacionesArticuloResponse(
        String articuloId,
        double promedio,
        int total,
        List<CalificacionResponse> calificaciones
) {
}
