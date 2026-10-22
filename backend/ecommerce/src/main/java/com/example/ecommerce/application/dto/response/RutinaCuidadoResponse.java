package com.example.ecommerce.application.dto.response;

import java.util.List;

public record RutinaCuidadoResponse(
        String id,
        String nombre,
        List<String> articulosIds
) {
}
