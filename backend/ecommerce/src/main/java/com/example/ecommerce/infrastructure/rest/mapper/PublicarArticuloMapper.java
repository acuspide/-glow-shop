
package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.response.PublicarArticuloResponse;
import com.example.ecommerce.domain.entity.Articulo;
import org.springframework.stereotype.Component;

@Component
public class PublicarArticuloMapper {

    public PublicarArticuloResponse toResponse(Articulo articulo) {
        return new PublicarArticuloResponse(
                articulo.getId(),
                articulo.getNombre().valor(),
                articulo.getPrecio().valor(),
                articulo.getCategoria().getId(),
                articulo.getCategoria().getNombre(),
                articulo.getMarca().getId(),
                articulo.getMarca().getNombre(),
                articulo.getTono().name(),
                articulo.getTiposPiel()
                        .stream()
                        .map(Enum::name)
                        .toList(),
                articulo.getCantidadDisponible(),
                articulo.getFechaVencimiento().fecha(),
                articulo.getTienda().getId(),
                articulo.getTienda().getNombre(),
                articulo.isPublicado()
        );
    }
}
