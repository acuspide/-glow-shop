package com.example.ecommerce.application.dto.response;

public record ConsultarInventarioResponse(
        long articuloId,
        String nombre,
        int cantidadDisponible
) {
}
