package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.response.RutinaCuidadoResponse;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RutinaCuidadoMapper {
    public RutinaCuidadoResponse toResponse(RutinaCuidado rutina) {

        List<Long> articulosIds = rutina.getArticulos()
                .stream()
                .map(articulo -> articulo.getId())
                .toList();

        return new RutinaCuidadoResponse(
                rutina.getId(),
                rutina.getNombre(),
                articulosIds
        );
    }
}
