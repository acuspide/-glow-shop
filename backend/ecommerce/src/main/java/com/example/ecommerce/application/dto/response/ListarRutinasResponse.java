package com.example.ecommerce.application.dto.response;
import java.util.List;
public record ListarRutinasResponse( List<RutinaResponse> rutinas
) {
    public record RutinaResponse(
            long id,
            String nombre,
            List<ArticuloResponse> articulos
    ) {
    }

    public record ArticuloResponse(
            long articuloId,
            String nombre,
            int cantidadDisponible
    ) {
    }
}
