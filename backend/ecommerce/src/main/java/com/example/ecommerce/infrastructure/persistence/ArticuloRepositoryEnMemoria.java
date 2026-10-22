package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Repository
public class ArticuloRepositoryEnMemoria implements ArticuloRepository {

    private final Map<String, Articulo> articulos = new HashMap<>();

    @Override
    public Optional<Articulo> obtenerPorId(String id) {
        return Optional.ofNullable(articulos.get(id));
    }

    @Override
    public void guardar(Articulo articulo) {
        articulos.put(articulo.getId(), articulo);
    }

    @Override
    public List<Articulo> listarTodos() {
        return List.copyOf(articulos.values());
    }
}
