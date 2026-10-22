package com.example.ecommerce.application.dto.response;

public record ConsultarInventarioResponse(
        String articuloId,
        String nombre,
        int cantidadDisponible
) {
}
