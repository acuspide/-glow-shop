package com.example.ecommerce.domain.repository;

import com.example.ecommerce.domain.entity.Tienda;

import java.util.Optional;

public interface TiendaRepository {

    void guardar(Tienda tienda);

    Optional<Tienda> obtenerPorId(String id);

    /** RN08: un vendedor administra una sola tienda. */
    boolean existePorVendedorId(String vendedorId);
}
