package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.response.ConsultarInventarioCompletoResponse;
import com.example.ecommerce.domain.entity.Articulo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsultarInventarioCompletoMapper {
    public ConsultarInventarioCompletoResponse toResponse(
            List<Articulo> articulos) {

        List<ConsultarInventarioCompletoResponse.ArticuloInventarioResponse>
                articulosResponse = articulos.stream()
                .map(articulo ->
                        new ConsultarInventarioCompletoResponse
                                .ArticuloInventarioResponse(
                                articulo.getId(),
                                articulo.getNombre().valor(),
                                articulo.getCantidadDisponible()
                        ))
                .toList();

        return new ConsultarInventarioCompletoResponse(
                articulosResponse
        );
    }
}
