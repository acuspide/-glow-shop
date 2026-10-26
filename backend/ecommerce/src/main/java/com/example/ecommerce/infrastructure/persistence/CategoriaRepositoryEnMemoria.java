package com.example.ecommerce.infrastructure.persistence;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.repository.CategoriaRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class CategoriaRepositoryEnMemoria implements CategoriaRepository{
    private final Map<String, Categoria> categorias = new ConcurrentHashMap<>();

    @Override
    public void guardar(Categoria categoria) {
        categorias.put(categoria.getId(), categoria);
    }

    @Override
    public Optional<Categoria> obtenerPorId(String id) {
        return Optional.ofNullable(categorias.get(id));
    }

    @Override
    public boolean existePorNombre(String nombre) {
        String buscado = nombre.trim();
        return categorias.values().stream()
                .anyMatch(categoria -> categoria.getNombre().equalsIgnoreCase(buscado));
    }
}
