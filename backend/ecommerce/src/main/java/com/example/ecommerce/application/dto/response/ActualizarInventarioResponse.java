package com.example.ecommerce.application.dto.response;

public record ActualizarInventarioResponse(
        long id,
        int cantidadDisponible,
        boolean publicado
) {
}
