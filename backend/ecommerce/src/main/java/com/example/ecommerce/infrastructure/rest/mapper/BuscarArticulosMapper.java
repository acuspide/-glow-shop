package com.example.ecommerce.infrastructure.rest.mapper;
import com.example.ecommerce.application.dto.request.BuscarArticulosRequest;
import com.example.ecommerce.domain.valueobject.CriteriosBusquedaArticulo;
import org.springframework.stereotype.Component;

@Component
public class BuscarArticulosMapper {
    public CriteriosBusquedaArticulo toCriterios(
            BuscarArticulosRequest request) {

        return new CriteriosBusquedaArticulo(
                request.texto(),
                request.categoriaId(),
                request.marcaId(),
                request.tiendaId(),
                request.precioMinimo(),
                request.precioMaximo(),
                request.tono(),
                request.tipoPiel()
        );
    }
}
