package com.example.ecommerce.domain.repository;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.valueobject.CriteriosBusquedaArticulo;

import java.util.List;
import java.util.Optional;

public interface ArticuloRepository {
    Optional<Articulo> obtenerPorId(String id);

    void guardar(Articulo articulo);
    List<Articulo> listarTodos();

    /**
     * Artículos disponibles para la venta (publicados, no eliminados, no vencidos y con stock)
     * que cumplen los criterios. El filtrado y la paginación los hace la base de datos.
     * La página empieza en 0.
     */
    Pagina<Articulo> buscar(CriteriosBusquedaArticulo criterios, int pagina, int tamano);
}
