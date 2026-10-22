package com.example.ecommerce.application.dto.response;

public record ActualizarInventarioResponse(
        String id,
        int cantidadDisponible,
        boolean publicado
) {
}
