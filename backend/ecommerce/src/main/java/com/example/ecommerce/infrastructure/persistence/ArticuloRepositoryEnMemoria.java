package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@Repository
public class ArticuloRepositoryEnMemoria implements ArticuloRepository {

    private final Map<Long, Articulo> articulos = new HashMap<>();

    @Override
    public Optional<Articulo> obtenerPorId(long id) {
        return Optional.ofNullable(articulos.get(id));
    }

    @Override
    public void guardar(Articulo articulo) {
        articulos.put(articulo.getId(), articulo);
    }
}
