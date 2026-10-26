package com.example.ecommerce.domain.repository;
import com.example.ecommerce.domain.entity.Categoria;

import java.util.Optional;
public interface CategoriaRepository {
    void guardar(Categoria categoria);

    Optional<Categoria> obtenerPorId(String id);

    /** Compara sin distinguir mayúsculas/minúsculas. */
    boolean existePorNombre(String nombre);
}
