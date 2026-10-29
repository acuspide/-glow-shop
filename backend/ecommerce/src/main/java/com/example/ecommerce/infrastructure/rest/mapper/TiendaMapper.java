package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.response.TiendaResponse;
import com.example.ecommerce.domain.entity.Tienda;
import org.springframework.stereotype.Component;

@Component
public class TiendaMapper {

    public TiendaResponse toResponse(Tienda tienda) {
        return new TiendaResponse(
                tienda.getId(),
                tienda.getNombre(),
                tienda.getDescripcion(),
                tienda.getVendedorId(),
                tienda.getEstado().name()
        );
    }
}
