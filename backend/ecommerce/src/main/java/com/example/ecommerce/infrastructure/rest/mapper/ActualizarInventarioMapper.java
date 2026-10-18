package com.example.ecommerce.infrastructure.rest.mapper;
import com.example.ecommerce.application.dto.response.ActualizarInventarioResponse;
import com.example.ecommerce.domain.entity.Articulo;
import org.springframework.stereotype.Component;

@Component
public class ActualizarInventarioMapper {
    public ActualizarInventarioResponse toResponse(Articulo articulo) {
        return new ActualizarInventarioResponse(
                articulo.getId(),
                articulo.getCantidadDisponible(),
                articulo.isPublicado()
        );
    }
}
