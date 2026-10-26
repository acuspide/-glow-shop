package com.example.ecommerce.domain.service;
import com.example.ecommerce.domain.exception.CategoriaDuplicadaException;
import com.example.ecommerce.domain.repository.CategoriaRepository;

/**
 * Servicio de dominio: el nombre de la categoría es único (sin distinguir mayúsculas).
 * La regla necesita mirar TODAS las categorías (el repositorio), por eso no cabe en la entidad Categoria.
 */
public class VerificadorDeCategoriaUnica {
    private final CategoriaRepository categoriaRepository;

    public VerificadorDeCategoriaUnica(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public void verificarQueNoExista(String nombre) {
        if (categoriaRepository.existePorNombre(nombre)) {
            throw new CategoriaDuplicadaException();
        }
    }
}
