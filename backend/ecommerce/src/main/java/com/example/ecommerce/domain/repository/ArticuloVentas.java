package com.example.ecommerce.domain.repository;

/**
 * Una fila del ranking de artículos más vendidos: el artículo y la suma de unidades vendidas.
 * Se usa Long porque SUM de enteros devuelve Long en JPQL.
 */
public record ArticuloVentas(String articuloId, String nombre, Long unidadesVendidas) {
}
