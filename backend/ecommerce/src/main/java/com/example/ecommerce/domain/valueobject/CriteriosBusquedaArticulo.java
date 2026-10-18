package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.entity.Articulo;
import java.math.BigDecimal;

public record CriteriosBusquedaArticulo(
        String texto,
        Long categoriaId,
        Long marcaId,
        Long tiendaId,
        BigDecimal precioMinimo,
        BigDecimal precioMaximo,
        Tono tono,
        TipoPiel tipoPiel
) {
    public boolean admite(Articulo articulo) {
        return (texto == null || texto.isBlank()
                || articulo.coincideConTexto(texto))
                && (categoriaId == null
                || articulo.perteneceACategoria(categoriaId))
                && (marcaId == null
                || articulo.esDeMarca(marcaId))
                && (tiendaId == null
                || articulo.perteneceATienda(tiendaId))
                && (precioMinimo == null
                || articulo.tienePrecioMinimo(precioMinimo))
                && (precioMaximo == null
                || articulo.tienePrecioMaximo(precioMaximo))
                && (tono == null
                || articulo.tieneTono(tono))
                && (tipoPiel == null
                || articulo.esCompatibleConTipoPiel(tipoPiel));
    }
}
