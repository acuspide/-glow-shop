package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.response.ConsultarInventarioResponse;
import com.example.ecommerce.domain.entity.Articulo;
import org.springframework.stereotype.Component;

@Component
public class ConsultarInventarioMapper {
    public ConsultarInventarioResponse toResponse(Articulo articulo) {
        return new ConsultarInventarioResponse(
                articulo.getId(),
                articulo.getNombre().valor(),
                articulo.getCantidadDisponible()
        );
    }
}
