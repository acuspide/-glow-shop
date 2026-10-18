package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.response.ListarArticulosResponse;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarArticulosMapper {
    public ListarArticulosResponse toResponse(Articulo articulo) {
        return new ListarArticulosResponse(
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
                        .map(TipoPiel::name)
                        .toList(),
                articulo.getCantidadDisponible(),
                articulo.getFechaVencimiento().fecha(),
                articulo.getTienda().getId(),
                articulo.getTienda().getNombre()
        );
    }

    public List<ListarArticulosResponse> toResponseList(
            List<Articulo> articulos
    ) {
        return articulos.stream()
                .map(this::toResponse)
                .toList();
    }
}
