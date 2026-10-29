package com.example.ecommerce.application.dto.response;

public record TiendaResponse(
        String id,
        String nombre,
        String descripcion,
        String vendedorId,
        String estado
) {
}
