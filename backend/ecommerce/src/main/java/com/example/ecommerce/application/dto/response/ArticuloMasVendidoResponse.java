package com.example.ecommerce.application.dto.response;

import com.example.ecommerce.domain.repository.ArticuloVentas;

public record ArticuloMasVendidoResponse(String articuloId, String nombre, long unidadesVendidas) {

    public static ArticuloMasVendidoResponse desde(ArticuloVentas fila) {
        return new ArticuloMasVendidoResponse(fila.articuloId(), fila.nombre(), fila.unidadesVendidas());
    }
}