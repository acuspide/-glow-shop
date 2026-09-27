package com.example.ecommerce.application.dto.response;

import java.util.List;

public record RutinaCuidadoResponse(
        long id,
        String nombre,
        List<Long> articulosIds
) {
}
