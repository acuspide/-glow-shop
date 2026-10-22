package com.example.ecommerce.application.dto.response;

import java.util.List;

public record ConsultarInventarioCompletoResponse(List<ArticuloInventarioResponse> articulos) {
    public record ArticuloInventarioResponse(
            String articuloId,
            String nombre,
            int cantidadDisponible
    ) {
    }
}
