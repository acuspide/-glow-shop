
package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.request.PublicarArticuloRequest;
import com.example.ecommerce.application.dto.response.PublicarArticuloResponse;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Inventario;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.valueobject.EstadoTienda;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import org.springframework.stereotype.Component;

@Component
public class PublicarArticuloMapper {

    public Articulo toEntity(String id, PublicarArticuloRequest request) {
        return Articulo.crear(
                id,
                new NombreArticulo(request.nombre()),
                new Precio(request.precio()),
                Categoria.crear(request.categoriaId(), request.categoriaNombre()),
                Marca.crear(request.marcaId(), request.marcaNombre()),
                request.tono(),
                request.tiposPiel(),
                Inventario.crear(request.cantidadDisponible()),
                new FechaVencimiento(request.fechaVencimiento()),
                // TEMPORAL: en el commit de publicar artículo la tienda se carga de su repositorio y se valida.
                Tienda.reconstruir(request.tiendaId(), request.tiendaNombre(), "", "", EstadoTienda.ACTIVA)
        );
    }

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
