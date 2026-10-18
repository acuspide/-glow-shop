package com.example.ecommerce.domain.repository;

import com.example.ecommerce.domain.entity.Articulo;

import java.util.List;
import java.util.Optional;

public interface ArticuloRepository {
    Optional<Articulo> obtenerPorId(long id);

    void guardar(Articulo articulo);
    List<Articulo> listarTodos();
}
